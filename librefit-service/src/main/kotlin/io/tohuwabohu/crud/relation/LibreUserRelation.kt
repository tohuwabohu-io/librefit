package io.tohuwabohu.crud.relation

import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

class LibreUserCompositeKey : Serializable {
    var userId: Long = 0L
    var added: LocalDate? = null
    var id: Long = 0L
}

@IdClass(LibreUserCompositeKey::class)
@MappedSuperclass
abstract class LibreUserWeakEntity : PanacheEntityBase {
    @Id
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L

    @Id
    @Column(nullable = false, columnDefinition = "date default current_timestamp")
    var added: LocalDate? = null

    @Id
    @Column(nullable = false)
    var id: Long = 0L
}
