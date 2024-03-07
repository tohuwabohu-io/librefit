package io.tohuwabohu.crud

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import java.time.LocalDateTime
import java.util.*

@Entity
@NamedQueries(
    NamedQuery(name = "WeightTrackerEntry.findLast", query = "from WeightTrackerEntry where userId = ?1 order by added desc, sequence desc, userId limit 1")
)
data class WeightTrackerEntry (
    @field:Min(value = 0, message = "Your weight should not be less than zero.")
    var amount: Float = 0f,
    var updated: LocalDateTime? = null
) : LibreUserWeakEntity() {
    @PreUpdate
    fun setUpdatedFlag() {
        updated = LocalDateTime.now()
    }
}

@ApplicationScoped
class WeightTrackerRepository : LibreUserRelatedRepository<WeightTrackerEntry>() {
    fun findLastEntry(userId: UUID): Uni<WeightTrackerEntry?> {
        return find("#WeightTrackerEntry.findLast", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }

}
