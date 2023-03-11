package io.tohuwabohu.crud

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.validation.ValidationError
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
class LibreUserRepository(private val validation: LibreUserValidation) : PanacheRepository<LibreUser> {

    fun createUser(user: LibreUser): Uni<LibreUser?> {
        validation.checkPassword(user)

        return find("email = ?1", user.email).firstResult()
            .onItem().ifNotNull().failWith{ ValidationError("A User with this E-Mail already exists.") }
            .onItem().ifNull().switchTo(persistAndFlush(user))
    }

    fun findByEmailAndPassword(email: String, password: String): Uni<LibreUser?> =
        find("email = ?1 and password = crypt(?2, password)", email, password).firstResult()
}

@ApplicationScoped
class LibreUserValidation {

    fun checkPassword(user: LibreUser) {
        if (user.password.isEmpty()) {
            throw ValidationError("The provided password is empty.")
        }
    }
}