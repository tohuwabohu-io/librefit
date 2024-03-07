package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@NamedQueries(
    NamedQuery(name = "AccountActivation.findByActivationId", query = "from AccountActivation where activationId = ?1 and validTo >= ?2")
)
data class AccountActivation (
    @Column(unique = true, nullable = false)
    var activationId: String,

    @Column(nullable = false)
    var validTo: LocalDateTime
): LibreUserWeakEntity()

@ApplicationScoped
class AccountActivationRepository : LibreUserRelatedRepository<AccountActivation>() {
    fun findByActivationId(activationId: String): Uni<AccountActivation?> {
        return find("#AccountActivation.findByActivationId", activationId, LocalDateTime.now()).firstResult()
            .onItem().ifNull().failWith(EntityNotFoundException())
    }

    @WithTransaction
    fun createAccountActivation(userId: UUID): Uni<AccountActivation> {
        val timeNow = LocalDateTime.now()
        val activationId = UUID.nameUUIDFromBytes("${userId}${timeNow}".toByteArray())

        val accountActivation = AccountActivation(
            activationId = activationId.toString(),
            validTo = timeNow.plusDays(14)
        )

        accountActivation.userId = userId

        return validateAndPersist(accountActivation)
    }

    @WithTransaction
    override fun validateAndPersist(entity: AccountActivation): Uni<AccountActivation> {
        super.validate(entity)

        // overwrite exising entry
        return findById(entity.getPrimaryKey()).onItem().transform { accountActivation ->
            accountActivation?.let { found ->
                found.activationId = entity.activationId
                found.validTo = entity.validTo
                found
            } ?: entity
        }.call { item ->
            Panache.getSession().call { s -> s.merge(item) }
        }
    }
}