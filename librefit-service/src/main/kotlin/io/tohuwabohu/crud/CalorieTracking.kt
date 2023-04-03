package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.quarkus.logging.Log
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.converter.CalorieTrackerCategoryConverter
import io.tohuwabohu.crud.error.UnmodifiedError
import io.tohuwabohu.crud.error.ValidationError
import io.tohuwabohu.crud.relation.LibreUserCompositeKey
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import io.tohuwabohu.crud.util.enumContains
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Tuple
import org.hibernate.Hibernate
import org.hibernate.annotations.SQLInsert
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EntityNotFoundException
import javax.persistence.PrePersist

@Entity
data class CalorieTrackerEntry (
    @Column(nullable = false)
    var amount: Float = 0f,

    @Convert(converter = CalorieTrackerCategoryConverter::class)
    @Column(nullable = false)
    var category: Category = Category.UNSET,

    var updated: LocalDateTime? = null,
    var description: String? = null
) : LibreUserWeakEntity() {
    @PrePersist
    fun setUpdatedFlag() {
        updated = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as CalorieTrackerEntry

        return userId == other.userId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(userId = $userId , amount = $amount , category = $category , updated = $updated , description = $description , added = $added , id = $id )"
    }
}


enum class Category {
    BREAKFAST, LUNCH, DINNER, SNACK, UNSET;
}

@ApplicationScoped
class CalorieTrackerRepository(val validation: CalorieTrackerValidation) : LibreUserRelatedRepository<CalorieTrackerEntry>() {
    @Inject
    lateinit var client: PgPool

    @ReactiveTransactional
    fun create(calorieTrackerEntry: CalorieTrackerEntry): Uni<CalorieTrackerEntry> {
        validation.checkEntry(calorieTrackerEntry)

        return persistAndFlush(calorieTrackerEntry)
    }

    @ReactiveTransactional
    fun updateTrackingEntry(calorieTrackerEntry: CalorieTrackerEntry): Uni<Int> {
        // TODO verify that entry belongs to logged in user -> return 404
        validation.checkEntry(calorieTrackerEntry)

        return findById(calorieTrackerEntry.getPrimaryKey()).onItem().ifNull()
            .failWith(EntityNotFoundException()).onItem().ifNotNull().transformToUni { entry ->
                val key = entry.getPrimaryKey()

                update(
                    "amount = ?1, category = ?2, description = ?3 where userId = ?4 and added = ?5 and id = ?6",
                    calorieTrackerEntry.amount,
                    calorieTrackerEntry.category,
                    calorieTrackerEntry.description.let { "" },
                    key.userId,
                    key.added,
                    key.id
                )
            }.onItem().ifNull().failWith { UnmodifiedError(calorieTrackerEntry.toString()) }
    }

    fun listDatesForUser(userId: Long): Uni<List<LocalDate>?> {
        validation.checkUserId(userId)

        return client.preparedQuery("select added from calorie_tracker_entry where user_id = $1 group by added")
            .execute(Tuple.of(userId)).onItem().ifNotNull()
            .transformToMulti { rowSet -> Multi.createFrom().iterable(rowSet) }.onItem()
            .transform { row -> row.getLocalDate("added") }.collect().asList().onItem()
            .invoke { list -> list.sortDescending() }.onFailure().invoke { throwable -> Log.error(throwable) }
    }
}

@ApplicationScoped
class CalorieTrackerValidation {
    fun checkEntry(entry: CalorieTrackerEntry) {
        // TODO check userId integrity

        checkUserId(entry.userId)
        // checkAddedDate(entry.added)

        if (entry.amount == null || entry.amount <= 0f) {
            throw ValidationError("The amount specified is invalid.")
        } else if (!enumContains<Category>(entry.category.name)) {
            throw ValidationError("The chosen category is invalid.");
        }

    }

    fun checkUserId(userId: Long) {
        if (userId <= 0) {
            throw IllegalArgumentException("Unauthorized Access.")
        }
    }

    fun checkAddedDate(added: LocalDate?) {
        if (added != null && added.isAfter(LocalDateTime.now().toLocalDate())) {
            throw IllegalArgumentException("This date lies in the future. $added")
        }
    }
}