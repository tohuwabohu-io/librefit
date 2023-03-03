package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.transaction.Transactional

@Entity
class WeightTrackerEntry {
    @Id
    @GeneratedValue
    var id: Long? = null
    var userId: Long? = null
    var amount: Float? = null
    lateinit var added: LocalDateTime
    lateinit var updated: LocalDateTime
}

@ApplicationScoped
class WeightTrackerRepository : PanacheRepository<WeightTrackerEntry> {
    fun listForUser(userId: Long) = find("userId", userId).list()

    @Transactional
    fun create(weightTrackerEntry: WeightTrackerEntry) = persist(weightTrackerEntry)

    @Transactional
    fun updateTrackingEntry(weightTrackerEntry: WeightTrackerEntry) = update(
        "amount = ?1, updated = ?2 where id=?3",
        weightTrackerEntry.amount!!, LocalDateTime.now(), weightTrackerEntry.id!!
    )
}