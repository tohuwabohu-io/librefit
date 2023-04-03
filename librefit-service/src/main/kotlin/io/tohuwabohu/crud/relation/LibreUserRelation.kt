package io.tohuwabohu.crud.relation

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.Uni
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

class LibreUserCompositeKey(
    var userId: Long = 0L,
    var added: LocalDate = LocalDate.now(),
    var id: Long = 0L
): Serializable

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

    @Transient
    @JsonIgnore
    fun getPrimaryKey(): LibreUserCompositeKey {
        return LibreUserCompositeKey(
            userId, added, id
        )
    }
}
abstract class LibreUserRelatedRepository<Entity : LibreUserWeakEntity> : PanacheRepositoryBase<Entity, LibreUserCompositeKey> {
    fun readEntry(userId: Long, date: LocalDate, id: Long): Uni<Entity> {
        // TODO verify that entry belongs to logged in user -> return 404

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

    @ReactiveTransactional
    fun deleteTrackingEntry(userId: Long, date: LocalDate, id: Long): Uni<Boolean> {
        val key = LibreUserCompositeKey(
            userId = userId,
            added = date,
            id = id
        )

        return findById(key).onItem().ifNull().failWith(EntityNotFoundException()).onItem()
            .ifNotNull().transformToUni { entry -> deleteById(entry.getPrimaryKey())}
    }
}