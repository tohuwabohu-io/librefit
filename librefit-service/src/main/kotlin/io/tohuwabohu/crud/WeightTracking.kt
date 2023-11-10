package io.tohuwabohu.crud

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@NamedQueries(
    NamedQuery(name = "WeightTrackerEntry.findLast", query = "from WeightTrackerEntry where userId = ?1 order by added desc, id desc, userId limit 1")
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as WeightTrackerEntry

        return userId == other.userId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(userId = $userId , amount = $amount , updated = $updated , added = $added , id = $id )"
    }
}

@ApplicationScoped
class WeightTrackerRepository : LibreUserRelatedRepository<WeightTrackerEntry>() {
    fun findLastEntry(userId: Long): Uni<WeightTrackerEntry?> {
        return find("#WeightTrackerEntry.findLast", userId).firstResult()
            .onItem().ifNull().failWith { EntityNotFoundException() }
    }

}
