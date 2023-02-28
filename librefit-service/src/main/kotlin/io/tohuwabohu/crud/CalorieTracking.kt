package io.tohuwabohu.crud

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.transaction.Transactional

@Entity
class CalorieTrackerEntry {
    @Id
    @GeneratedValue
    var id: Long? = null
    var userId: Long? = null
    var amount: Float? = null
    lateinit var added: LocalDateTime
    lateinit var updated: LocalDateTime
    lateinit var description: String
}

@ApplicationScoped
class CalorieTrackerRepository : PanacheRepository<CalorieTrackerEntry> {
    fun listForUser(userId: Long) = find("userId", userId).list()

    @Transactional
    fun create(calorieTrackerEntry: CalorieTrackerEntry) = persist(calorieTrackerEntry)

    @Transactional
    fun updateTrackingEntry(calorieTrackerEntry: CalorieTrackerEntry) = update(
        "amount = ?1, description = ?2, updated = ?3 where id=?4",
        calorieTrackerEntry.amount!!, calorieTrackerEntry.description, LocalDateTime.now(), calorieTrackerEntry.id!!
    )
}