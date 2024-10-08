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
    NamedQuery(name = "WeightTarget.findLast", query = "from WeightTarget where userId = ?1 order by sequence desc, added desc, userId limit 1")
)
data class WeightTarget(
    @field:NotNull(message = "The starting date of your target must not be empty.")
    var startDate: LocalDate? = null,

    @field:NotNull(message = "The end date of your target must not be empty.")
    var endDate: LocalDate? = null,

    @field:NotNull(message = "The initial weight of your target must not be empty.")
    @field:Min(value = 0, message = "The initial weight of your target must not be less than zero.")
    var initialWeight: Float,

    @field:NotNull(message = "The target weight of your target must not be empty.")
    @field:Min(value = 0, message = "The target weight of your target must not be less than zero.")
    var targetWeight: Float,

    var updated: LocalDateTime? = null
) : LibreUserWeakEntity() {
    @PreUpdate
    fun onUpdate() {
        updated = LocalDateTime.now()
    }
}

@ApplicationScoped
class WeightTargetRepository : LibreUserRelatedRepository<WeightTarget>() {
    fun findLatestWeightTarget(userId: UUID): Uni<WeightTarget?> {
        return find("#WeightTarget.findLast", userId).firstResult()
    }
}