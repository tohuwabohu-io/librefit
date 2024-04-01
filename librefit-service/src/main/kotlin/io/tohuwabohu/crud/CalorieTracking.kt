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
    NamedQuery(name = "CalorieTrackerEntry.listDates",
        query = "from CalorieTrackerEntry where userId = ?1 and added between ?2 and ?3 group by added, userId, sequence order by added desc, userId, sequence"
    )
)
data class CalorieTrackerEntry (
    @Column(nullable = false)
    @field:NotNull(message = "The amount of calories must not be empty.")
    @field:Min(value = 0, message = "The amount of calories must not be less than zero.")
    var amount: Float = 0f,

    @Column(nullable = false)
    var category: String,

    var updated: LocalDateTime? = null,
    var description: String? = null
) : LibreUserWeakEntity() {
    @PreUpdate
    fun onUpdate() {
        updated = LocalDateTime.now()
    }
}


@ApplicationScoped
class CalorieTrackerRepository : LibreUserRelatedRepository<CalorieTrackerEntry>() {
    fun listDatesForUser(userId: UUID, dateFrom: LocalDate, dateTo: LocalDate): Uni<Set<LocalDate>?> =
        find("#CalorieTrackerEntry.listDates", userId, dateFrom, dateTo)
            .list()
            .onItem().ifNotNull().transform { list -> list.map { entry -> entry.added }.toSet() }
            .onItem().ifNull().failWith(EntityNotFoundException())
}
