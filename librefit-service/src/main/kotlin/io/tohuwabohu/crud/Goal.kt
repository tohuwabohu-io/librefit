package io.tohuwabohu.crud

import io.smallrye.mutiny.Uni
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
        return find("userId = ?1 order by added desc, id desc", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }
}