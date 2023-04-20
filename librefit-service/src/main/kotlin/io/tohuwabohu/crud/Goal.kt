package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.error.UnmodifiedError
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.persistence.EntityNotFoundException
import javax.persistence.PreUpdate
import javax.transaction.Transactional

@Entity
data class Goal (
    var startAmount: Float,
    var endAmount: Float,
    var startDate: LocalDateTime,
    var endDate: LocalDateTime,
    var updated: LocalDateTime
) : LibreUserWeakEntity() {
    @PreUpdate
    fun setUpdatedFlag() {
        updated = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Goal

        return userId == other.userId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(userId = $userId , startAmount = $startAmount , endAmount = $endAmount , startDate = $startDate , endDate = $endDate , added = $added , id = $id )"
    }
}

@ApplicationScoped
class GoalsRepository : LibreUserRelatedRepository<Goal>() {
    fun listByUser(userId: Number) = find("userId", userId).list()
    fun findLatestForUser(userId: Number) = find("userId", userId).list() //..minByOrNull { it.startDate }!!

    @ReactiveTransactional
    fun updateGoal(goal: Goal): Uni<Int> {
        return findById(goal.getPrimaryKey()).onItem().ifNull()
            .failWith(EntityNotFoundException()).onItem().ifNotNull().transformToUni{ entry ->
                val key = entry.getPrimaryKey()

                update(
                    "startAmount = ?1, endAmount = ?2, startDate = ?3, endDate = ?4, updated = ?5 where user_id = ?6 and added = ?7 and id = ?8",
                    goal.startAmount, goal.endAmount, goal.startDate, goal.endDate, goal.updated, key.userId, key.added, key.id
                )
            }.onItem().ifNull().failWith{ UnmodifiedError(goal.toString()) }
    }
}