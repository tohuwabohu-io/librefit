package io.tohuwabohu.crud.relation

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.error.ValidationError
import jakarta.inject.Inject
import jakarta.persistence.*
import jakarta.validation.Validator
import java.io.Serializable
import java.time.LocalDate

class LibreUserCompositeKey(
    var userId: Long = 0L,
    var added: LocalDate = LocalDate.now(),
    var id: Long = 0L
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LibreUserCompositeKey

        if (userId != other.userId) return false
        if (added != other.added) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + added.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun toString(): String {
        return "LibreUserCompositeKey(userId=$userId, added=$added, id=$id)"
    }
}

@IdClass(LibreUserCompositeKey::class)
@MappedSuperclass
abstract class LibreUserWeakEntity : PanacheEntityBase {
    @Id
    @Column(nullable = false)
    var userId: Long = 0L

    @Id
    @Column(nullable = false)
    var added: LocalDate = LocalDate.now()

    @Id
    @Column(nullable = false)
    var id: Long = 0L

    @JsonIgnore
    fun getPrimaryKey(): LibreUserCompositeKey {
        return LibreUserCompositeKey(
            userId, added, id
        )
    }
}
abstract class LibreUserRelatedRepository<Entity : LibreUserWeakEntity> : PanacheRepositoryBase<Entity, LibreUserCompositeKey> {
    @Inject
    lateinit var validator: Validator

    fun validate(entity: Entity) {
        val violations = validator.validate(entity)

        if (violations.isNotEmpty()) {
            throw ValidationError(violations.map { violation -> violation.message })
        }
    }

    @WithTransaction
    fun validateAndPersist(entity: Entity): Uni<Entity> {
        validate(entity)

        return persist(entity)
    }

    @WithTransaction
    fun updateEntry(entity: Entity, clazz: Class<Entity>): Uni<Entity> {
        return Panache.getSession().call { s ->
            s.find(clazz, entity.getPrimaryKey())
                .onItem().ifNull().failWith(EntityNotFoundException())
        }.chain { s -> s.merge(entity) }
    }

    fun readEntry(userId: Long, date: LocalDate, id: Long): Uni<Entity> {
        val key = LibreUserCompositeKey(
            userId = userId,
            added = date,
            id = id
        )

        return findById(key).onItem().ifNull().failWith(EntityNotFoundException())
    }

    fun listEntriesForUserAndDate(userId: Long, date: LocalDate): Uni<List<Entity>> {
        return list("userId = ?1 and added = ?2", userId, date)
    }

    fun listEntriesForUserAndDateRange(userId: Long, dateFrom: LocalDate, dateTo: LocalDate): Uni<List<Entity>> {
        return list("userId = ?1 and added between ?2 and ?3", userId, dateFrom, dateTo)
    }

    @WithTransaction
    fun deleteEntry(userId: Long, date: LocalDate, id: Long): Uni<Boolean> {
        val key = LibreUserCompositeKey(
            userId = userId,
            added = date,
            id = id
        )

        return findById(key).onItem().ifNull().failWith(EntityNotFoundException()).onItem()
            .ifNotNull().transformToUni { entry -> deleteById(entry.getPrimaryKey())}
    }
}