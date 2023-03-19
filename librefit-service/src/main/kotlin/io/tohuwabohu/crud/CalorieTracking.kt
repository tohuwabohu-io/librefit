package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.tohuwabohu.crud.converter.CalorieTrackerCategoryConverter
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.*
import javax.transaction.Transactional

@Entity
class CalorieTrackerEntry {
    @Id
    @GeneratedValue
    var id: Long? = null
    var userId: Long? = null
    var amount: Float? = null
    @Convert(converter = CalorieTrackerCategoryConverter::class)
    lateinit var category: Category
    lateinit var added: LocalDateTime
    var updated: LocalDateTime? = null
    var description: String? = null
}

enum class Category {
    BREAKFAST, LUNCH, DINNER, SNACK, UNSET;
}

@ApplicationScoped
class CalorieTrackerRepository : PanacheRepository<CalorieTrackerEntry> {
    fun listForUser(userId: Long) = find("userId", userId).list()

    @Transactional
    fun create(calorieTrackerEntry: CalorieTrackerEntry) = persist(calorieTrackerEntry)

    @Transactional
    fun updateTrackingEntry(calorieTrackerEntry: CalorieTrackerEntry) = update(
        "amount = ?1 updated = ?2 category = ?3 where id = ?4",
        calorieTrackerEntry.amount!!, LocalDateTime.now(), calorieTrackerEntry.category, calorieTrackerEntry.id!!
    )
}