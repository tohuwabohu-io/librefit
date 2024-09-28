package io.tohuwabohu.crud

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.user.LibreUserRelatedRepository
import io.tohuwabohu.crud.user.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Entity
import jakarta.persistence.NamedQueries
import jakarta.persistence.NamedQuery
import jakarta.persistence.PreUpdate
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

    @field:NotNull(message = "Target intake must not be empty.")
    @field:Min(value = 1, message = "Target intake must be greater than zero.")
    var targetCalories: Float,

    @field:NotNull(message = "Maximum amount must not be empty.")
    @field:Min(value = 1, message = "Maximum amount must be greater than zero.")
    var maximumCalories: Float,

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
    }
}