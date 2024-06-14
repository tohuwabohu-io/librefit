package io.tohuwabohu.crud

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@NamedQueries(
    NamedQuery(name = "Goal.findLast", query = "from Goal where userId = ?1 order by sequence desc, added desc, userId limit 1")
)
data class Goal(
    @field:NotNull(message = "The initial weight of your goal must not be empty.")
    @field:Min(value = 0, message = "The initial weight of your goal must not be less than zero.")
    var initialWeight: Float,

    @field:NotNull(message = "The target weight of your goal must not be empty.")
    @field:Min(value = 0, message = "The target weight of your goal must not be less than zero.")
    var targetWeight: Float,

    @field:NotNull(message = "The starting date of your goal must not be empty.")
    var startDate: LocalDate,

    @field:NotNull(message = "The end date of your goal must not be empty.")
    var endDate: LocalDate,

    @Column(nullable = true)
    @field:Min(value = 0, message = "The target calorie amount must not be less than zero.")
    var targetCalories: Float? = null,

    @Column(nullable = true)
    @field:Min(value = 0, message = "The maximum calorie amount must not be less than zero.")
    var maximumCalories: Float? = null,

    var updated: LocalDateTime? = null
) : LibreUserWeakEntity() {
    @PreUpdate
    fun onUpdate() {
        updated = LocalDateTime.now()
    }
}

@ApplicationScoped
class GoalsRepository : LibreUserRelatedRepository<Goal>() {
    fun findLastGoal(userId: UUID): Uni<Goal?> {
        return find("#Goal.findLast", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }
}