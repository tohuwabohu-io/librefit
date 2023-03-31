package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity

@Entity
data class WeightTrackerEntry (
    var amount: Float,
    var updated: LocalDateTime
) : LibreUserWeakEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as WeightTrackerEntry

        return userId == other.userId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(userId = $userId , amount = $amount , updated = $updated , added = $added , id = $id )"
    }
}

@ApplicationScoped
class WeightTrackerRepository : PanacheRepository<WeightTrackerEntry> {
    fun listForUser(userId: Long) = find("userId", userId).list()

    @ReactiveTransactional
    fun create(weightTrackerEntry: WeightTrackerEntry) = persist(weightTrackerEntry)

    @ReactiveTransactional
    fun updateTrackingEntry(weightTrackerEntry: WeightTrackerEntry) {
        val key = weightTrackerEntry.getPrimaryKey()

        update(
            "amount = ?1 where user_id = ?2 and added = ?3 and id = ?4",
            weightTrackerEntry.amount, key.userId, key.added, key.id
        )
    }
}