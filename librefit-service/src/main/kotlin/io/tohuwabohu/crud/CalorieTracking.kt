package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.quarkus.logging.Log
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.converter.CalorieTrackerCategoryConverter
import io.tohuwabohu.crud.util.enumContains
import io.tohuwabohu.crud.validation.ValidationError
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Tuple
import java.time.LocalDate
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class CalorieTrackerEntry {
    @Id
    @GeneratedValue
    var id: Long? = null
    var userId: Long? = null
    var amount: Float? = null
    @Convert(converter = CalorieTrackerCategoryConverter::class)
    lateinit var category: Category
    lateinit var added: LocalDate
    var updated: LocalDateTime? = null
    var description: String? = null

    override fun toString(): String {
        return "CalorieTrackerEntry<userId=$userId,amount=$amount,category=$category,added=$added,updated=$updated>"
    }
}

enum class Category {
    BREAKFAST, LUNCH, DINNER, SNACK, UNSET;
}

@ApplicationScoped
class CalorieTrackerRepository(val validation: CalorieTrackerValidation) : PanacheRepository<CalorieTrackerEntry> {
    @Inject
    lateinit var client: PgPool

    fun create(calorieTrackerEntry: CalorieTrackerEntry): Uni<CalorieTrackerEntry> {
        validation.checkEntry(calorieTrackerEntry)

        return persistAndFlush(calorieTrackerEntry)
    }

    fun updateTrackingEntry(calorieTrackerEntry: CalorieTrackerEntry) = update(
        "amount = ?1 updated = ?2 category = ?3 where id = ?4",
        calorieTrackerEntry.amount!!, LocalDateTime.now(), calorieTrackerEntry.category, calorieTrackerEntry.id!!
    )

    fun listDatesForUser(userId: Long): Uni<List<LocalDate>?> {
        validation.checkUserId(userId)

        return client.preparedQuery("select added from calorie_tracker_entry where user_id = $1 group by added")
            .execute(Tuple.of(userId))
            .onItem().ifNotNull().transformToMulti{ rowSet -> Multi.createFrom().iterable(rowSet)}
            .onItem().transform { row -> row.getLocalDate("added")}
            .collect().asList()
            .onItem().invoke{ list -> list.sortDescending() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
    }

    fun listEntriesForUserAndDate(userId: Long, date: LocalDate): Uni<List<CalorieTrackerEntry>> {
        validation.checkUserId(userId)
        validation.checkAddedDate(added = date)

        return list("userId = ?1 and added = ?2", userId, date)
    }
}

@ApplicationScoped
class CalorieTrackerValidation {
    fun checkEntry(entry: CalorieTrackerEntry) {
        // TODO check userId integrity

        checkUserId(entry.userId!!)
        checkAddedDate(entry.added)

        if (entry.amount == null || entry.amount!! <= 0f) {
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

    fun checkAddedDate(added: LocalDate) {
        if (added.isAfter(LocalDateTime.now().toLocalDate())) {
            throw IllegalArgumentException("This date lies in the future. $added")
        }
    }
}