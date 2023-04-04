package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.quarkus.logging.Log
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.converter.CalorieTrackerCategoryConverter
import io.tohuwabohu.crud.error.UnmodifiedError
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Tuple
import org.hibernate.Hibernate
import java.time.LocalDate
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
data class CalorieTrackerEntry (
    @Column(nullable = false)
    @field:NotNull(message = "The amount of calories must not be empty.")
    @field:Min(value = 0, message = "The amount of calories must not be less than zero.")
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
class CalorieTrackerRepository : LibreUserRelatedRepository<CalorieTrackerEntry>() {
    @Inject
    lateinit var client: PgPool

    @ReactiveTransactional
    fun updateTrackingEntry(calorieTrackerEntry: CalorieTrackerEntry): Uni<Int> {
        // TODO verify that entry belongs to logged in user -> return 404
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
        return client.preparedQuery("select added from calorie_tracker_entry where user_id = $1 group by added")
            .execute(Tuple.of(userId)).onItem().ifNotNull()
            .transformToMulti { rowSet -> Multi.createFrom().iterable(rowSet) }.onItem()
            .transform { row -> row.getLocalDate("added") }.collect().asList().onItem()
            .invoke { list -> list.sortDescending() }.onFailure().invoke { throwable -> Log.error(throwable) }
    }
}
