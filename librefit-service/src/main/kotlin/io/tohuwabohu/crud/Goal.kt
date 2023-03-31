package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.transaction.Transactional

@Entity
data class Goal (
    var startAmount: Float,
    var endAmount: Float,
    var startDate: LocalDateTime,
    var endDate: LocalDateTime,
) : LibreUserWeakEntity() {
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
class GoalsRepository : PanacheRepository<Goal> {
    fun listByUser(userId: Number) = find("userId", userId).list()
    fun findLatestForUser(userId: Number) = find("userId", userId).list() //..minByOrNull { it.startDate }!!

    @ReactiveTransactional
    fun create(goal: Goal) = persist(goal)

    @ReactiveTransactional
    fun updateGoal(goal: Goal) = update(
        "startDate = ?1, endDate = ?2, startAmount = ?3, endAmount = ?4",
        goal.startDate, goal.endDate, goal.startAmount!!, goal.endAmount!!
    )
}