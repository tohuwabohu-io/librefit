package io.tohuwabohu.crud

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.smallrye.mutiny.Uni
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Cacheable
import javax.persistence.Entity

@Entity
@Cacheable
class LibreUser: PanacheEntity() {
    lateinit var name: String
    @JsonIgnore
    lateinit var password: String
    lateinit var email: String
    lateinit var registered: LocalDateTime
    var lastLogin: LocalDateTime? = null

    override fun toString(): String {
        return "LibreUser<name=$name,email=$email,registered=$registered,lastLogin=$lastLogin>"
    }
}

@ApplicationScoped
class LibreUserRepository : PanacheRepository<LibreUser> {
    fun findByEmailAndPassword(email: String, password: String): Uni<LibreUser?> =
        find("email = ?1 and password = crypt(?2, password)", email, password).firstResult()
}