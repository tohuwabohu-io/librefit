package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.error.UnmodifiedError
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Entity
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class Goal(
    @field:NotNull(message = "The initial amount of your goal must not be empty.")
    @field:Min(value = 0, message = "The initial amount of your goal must not be less than zero.")
    var startAmount: Float,
    @field:NotNull(message = "The target amount of your goal must not be empty.")
    @field:Min(value = 0, message = "The target amount of your goal must not be less than zero.")
    var endAmount: Float,
    @field:NotNull(message = "The starting date of your goal must not be empty.")
    var startDate: LocalDate,
    @field:NotNull(message = "The end date of your goal must not be empty.")
    var endDate: LocalDate,
    var updated: LocalDateTime? = null
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
class GoalsRepository : LibreUserRelatedRepository<Goal>() {
    fun findLastGoal(userId: Number): Uni<Goal?> {
        return find("user_id = ?1 order by added desc, id desc", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }

    @WithTransaction
    fun updateGoal(goal: Goal): Uni<Int> {
        return findById(goal.getPrimaryKey()).onItem().ifNull()
            .failWith(EntityNotFoundException()).onItem().ifNotNull().transformToUni{ entry ->
                val key = entry.getPrimaryKey()

                update(
                    "startAmount = ?1, endAmount = ?2, startDate = ?3, endDate = ?4, updated = ?5 where user_id = ?6 and added = ?7 and id = ?8",
                    goal.startAmount, goal.endAmount, goal.startDate, goal.endDate, LocalDateTime.now(), key.userId, key.added, key.id
                )
            }.onItem().ifNull().failWith{ UnmodifiedError(goal.toString()) }
    }
}