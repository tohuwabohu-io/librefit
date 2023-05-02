package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.error.UnmodifiedError
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import io.vertx.mutiny.pgclient.PgPool
import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.Entity
import javax.persistence.EntityNotFoundException
import javax.persistence.PreUpdate
import javax.validation.constraints.Min

@Entity
data class WeightTrackerEntry (
    @field:Min(value = 0, message = "Your weight should not be less than zero.")
    var amount: Float = 0f,
    var updated: LocalDateTime? = null
) : LibreUserWeakEntity() {
    @PreUpdate
    fun setUpdatedFlag() {
        updated = LocalDateTime.now()
    }

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
class WeightTrackerRepository : LibreUserRelatedRepository<WeightTrackerEntry>() {
    @ReactiveTransactional
    fun updateTrackingEntry(weightTrackerEntry: WeightTrackerEntry): Uni<Int> {
        // TODO verify that entry belongs to logged in user -> return 404

        return findById(weightTrackerEntry.getPrimaryKey()).onItem().ifNull()
            .failWith(EntityNotFoundException()).onItem().ifNotNull().transformToUni{ entry ->
                val key = entry.getPrimaryKey()

                update(
                    "amount = ?1, updated = ?2 where user_id = ?3 and added = ?4 and id = ?5",
                    weightTrackerEntry.amount, LocalDateTime.now(), key.userId, key.added, key.id
                )
            }.onItem().ifNull().failWith{ UnmodifiedError(weightTrackerEntry.toString()) }
    }

    fun findLastEntry(userId: Long): Uni<WeightTrackerEntry?> {
        return find("user_id = ?1 order by added desc, id desc", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }

}
