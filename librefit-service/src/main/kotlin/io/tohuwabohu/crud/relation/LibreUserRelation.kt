package io.tohuwabohu.crud.relation

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
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
