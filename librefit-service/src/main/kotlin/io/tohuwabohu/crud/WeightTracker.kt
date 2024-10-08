package io.tohuwabohu.crud

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.user.LibreUserRelatedRepository
import io.tohuwabohu.crud.user.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@NamedQueries(
    NamedQuery(name = "WeightTracker.findLast", query = "from WeightTracker where userId = ?1 order by sequence desc, added desc, userId limit 1"),
    NamedQuery(name = "WeightTracker.listDates",
        query = "from WeightTracker where userId = ?1 and added between ?2 and ?3 group by added, userId, sequence order by added desc, userId, sequence"
    )
)
data class WeightTracker (
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
class WeightTrackerRepository : LibreUserRelatedRepository<WeightTracker>() {
    fun findLastEntry(userId: UUID): Uni<WeightTracker?> {
        return find("#WeightTracker.findLast", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }

    fun listDatesForUser(userId: UUID, dateFrom: LocalDate, dateTo: LocalDate): Uni<Set<LocalDate>?> =
        find("#WeightTracker.listDates", userId, dateFrom, dateTo)
            .list()
            .onItem().ifNotNull().transform { list -> list.map { entry -> entry.added }.toSet() }
            .onItem().ifNull().failWith(EntityNotFoundException())
}
