package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.transaction.Transactional

@Entity
class Goal : LibreUserWeakEntity() {
    var startAmount: Float? = null
    var endAmount: Float? = null
    lateinit var startDate: LocalDateTime
    lateinit var endDate: LocalDateTime
}

@ApplicationScoped
class GoalsRepository : PanacheRepository<Goal> {
    fun listByUser(userId: Number) = find("userId", userId).list()
    fun findLatestForUser(userId: Number) = find("userId", userId).list() //..minByOrNull { it.startDate }!!

    @Transactional
    fun create(goal: Goal) = persist(goal)

    @Transactional
    fun updateGoal(goal: Goal) = update(
        "startDate = ?1, endDate = ?2, startAmount = ?3, endAmount = ?4",
        goal.startDate, goal.endDate, goal.startAmount!!, goal.endAmount!!
    )
}