package io.tohuwabohu.crud.user

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.ImportConfig
import io.tohuwabohu.crud.error.ErrorDescription
import io.tohuwabohu.crud.error.ValidationError
import jakarta.inject.Inject
import jakarta.persistence.*
import jakarta.validation.Validator
import java.io.Serializable
import java.time.LocalDate
import java.util.*

class LibreUserCompositeKey(
    var userId: UUID? = null,
    var added: LocalDate = LocalDate.now(),
    var sequence: Long = 0L
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibreUserCompositeKey

        if (userId != other.userId) return false
        if (added != other.added) return false
        if (sequence != other.sequence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + added.hashCode()
        result = 31 * result + sequence.hashCode()
        return result
    }

    override fun toString(): String {
        return "LibreUserCompositeKey(userId=$userId, added=$added, id=$sequence)"
    }
}

@IdClass(LibreUserCompositeKey::class)
@MappedSuperclass
abstract class LibreUserWeakEntity : PanacheEntityBase {
    @Id
    @Column(nullable = false)
    var userId: UUID? = null

    @Id
    @Column(nullable = false)
    var added: LocalDate = LocalDate.now()

    @Id
    @Column(nullable = false)
    var sequence: Long? = 1L

    @JsonIgnore
    fun getPrimaryKey(): LibreUserCompositeKey {
        return LibreUserCompositeKey(
            userId!!, added, sequence!!
        )
    }
}

object LibreUserSecurity {
    @JsonIgnore
    fun <Entity: LibreUserWeakEntity> withPrincipal(securityIdentity: SecurityIdentity, entity: Entity): Entity {
        entity.userId = UUID.fromString(securityIdentity.principal.name)

        return entity
    }
}

abstract class LibreUserRelatedRepository<Entity : LibreUserWeakEntity> : PanacheRepositoryBase<Entity, LibreUserCompositeKey> {
    @Inject
    lateinit var validator: Validator

    fun withPrincipal(securityIdentity: SecurityIdentity, entity: Entity): LibreUserRelatedRepository<Entity> {
        entity.userId = UUID.fromString(securityIdentity.principal.name)

        return this
    }

    private fun validate(entity: Entity) {
        val violations = validator.validate(entity)

        if (violations.isNotEmpty()) {
            val errors = violations.map { violation ->
                ErrorDescription(violation.propertyPath.filterNotNull()[0].name, violation.message)
            }

            throw ValidationError(errors)
        }
    }

    @WithTransaction
    open fun validateAndPersist(entity: Entity): Uni<Entity> {
        validate(entity)

        return find("userId = ?1 and added = ?2 order by sequence desc, added, userId", entity.userId!!, entity.added).firstResult()
            .onItem().transform { item ->
                if (item != null) {
                    entity.sequence = item.sequence?.plus(1L)
                }

                entity
            }.call { e ->
                persist(e!!)
            }
    }

    @WithTransaction
    fun updateEntry(entity: Entity, clazz: Class<Entity>): Uni<Entity> {
        return Panache.getSession().call { s ->
            s.find(clazz, entity.getPrimaryKey())
                .onItem().ifNull().failWith(EntityNotFoundException())
                .replaceWith(validate(entity))
        }.chain { s -> s.merge(entity) }
    }

    fun readEntry(userId: UUID, date: LocalDate, id: Long): Uni<Entity> {
        val key = LibreUserCompositeKey(
            userId = userId,
            added = date,
            sequence = id
        )

        return findById(key).onItem().ifNull().failWith(EntityNotFoundException())
    }

    fun listEntriesForUserAndDate(userId: UUID, date: LocalDate): Uni<List<Entity>> {
        return list("userId = ?1 and added = ?2 order by sequence desc", userId, date)
    }

    fun listEntriesForUserAndDateRange(userId: UUID, dateFrom: LocalDate, dateTo: LocalDate): Uni<List<Entity>> {
        return list("userId = ?1 and added between ?2 and ?3 order by added desc", userId, dateFrom, dateTo)
    }

    private fun deleteEntriesForUserAndDate(userId: UUID, date: List<LocalDate>): Uni<Long> {
        return delete("userId = ?1 and added in (?2)", userId, date)
    }

    fun listEntriesForUser(userId: UUID): Uni<List<Entity>> {
        return list("userId = ?1", userId)
    }

    @WithTransaction
    fun deleteEntry(userId: UUID, date: LocalDate, sequence: Long): Uni<Boolean> {
        val key = LibreUserCompositeKey(
            userId = userId,
            added = date,
            sequence = sequence
        )

        return deleteEntry(key)
    }

    @WithTransaction
    fun deleteEntry(key: LibreUserCompositeKey): Uni<Boolean> {
        return findById(key).onItem().ifNull().failWith(EntityNotFoundException()).onItem()
            .ifNotNull().transformToUni { entry -> deleteById(entry.getPrimaryKey())}
    }

    @WithTransaction
    fun importBulk(entries: List<Entity>, config: ImportConfig): Uni<Void> {
        val persistFlow = Multi.createFrom().iterable(entries.sortedBy { it.added } )
            .onItem().call { entity -> validateAndPersist(entity) }.collect().asList().replaceWithVoid()

        return if (!config.drop) {
            persistFlow
        } else {
            val dates = entries.map { it.added }.distinct()
            val userId = entries.first().userId!!

            deleteEntriesForUserAndDate(userId, dates).replaceWith(persistFlow)
        }
    }
}