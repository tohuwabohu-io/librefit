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
    NamedQuery(name = "CalorieTarget.findLast", query = "from CalorieTarget where userId = ?1 order by sequence desc, added desc, userId limit 1")
)
data class CalorieTarget (
    @field:NotNull(message = "The starting date of your target must not be empty.")
    var startDate: LocalDate,

    @field:NotNull(message = "The end date of your target must not be empty.")
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
class CalorieTargetRepository : LibreUserRelatedRepository<CalorieTarget>() {
    fun findLatestCalorieTarget(userId: UUID): Uni<CalorieTarget?> {
        return find("#CalorieTarget.findLast", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }
}