package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity

@Entity
class WeightTrackerEntry : LibreUserWeakEntity() {
    var amount: Float? = null
    lateinit var updated: LocalDateTime
}

@ApplicationScoped
class WeightTrackerRepository : PanacheRepository<WeightTrackerEntry> {
    fun listForUser(userId: Long) = find("userId", userId).list()

    fun create(weightTrackerEntry: WeightTrackerEntry) = persist(weightTrackerEntry)

    fun updateTrackingEntry(weightTrackerEntry: WeightTrackerEntry) = update(
        "amount = ?1, updated = ?2 where id=?3",
        weightTrackerEntry.amount!!, LocalDateTime.now(), weightTrackerEntry.id!!
    )
}