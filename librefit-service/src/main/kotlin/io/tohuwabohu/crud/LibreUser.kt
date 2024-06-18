package io.tohuwabohu.crud

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.security.UnauthorizedException
import io.quarkus.security.jpa.Password
import io.quarkus.security.jpa.Roles
import io.quarkus.security.jpa.UserDefinition
import io.quarkus.security.jpa.Username
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.unchecked.Unchecked
import io.tohuwabohu.crud.error.ErrorDescription
import io.tohuwabohu.crud.error.ValidationError
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.*
import jakarta.validation.Validator
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import java.util.*

@Entity
@UserDefinition
@NamedQueries(
    NamedQuery(name = "LibreUser.findByEmail", query = "from LibreUser where email = ?1")
)
data class LibreUser (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    var id: UUID? = null,

    @Username
    @Column(unique = true, nullable = false)
    @field:Email(message = "Please enter a valid e-mail address.", regexp = "^\\S+@\\S+\\.\\S+\$")
    @field:NotEmpty(message = "The provided e-mail address is empty.")
    var email: String,

    @Password // <-- automatic encryption with annotation does not work due to a bug
    @Column(nullable = false)
    @field:NotEmpty(message = "The provided password is empty.")
    @field:Length(min = 6, message = "Chosen password must be at least 6 characters long.")
    var password: String,

    @Roles
    @Column(nullable = false)
    var role: String = "User",

    @Column(nullable = true)
    var name: String? = null,
    var registered: LocalDateTime? = null,
    var lastLogin: LocalDateTime? = null,

    @Column(nullable = true)
    var avatar: String? = null,

    @Column(nullable = false)
    var activated: Boolean = false
): PanacheEntityBase {
    @PrePersist
    fun onInsert() {
        password = BcryptUtil.bcryptHash(password)
        registered = LocalDateTime.now()
    }
}

@ApplicationScoped
class LibreUserRepository : PanacheRepositoryBase<LibreUser, UUID> {
    @Inject
    private lateinit var validator: Validator

    fun findByEmail(email: String): Uni<LibreUser?> {
        return find("#LibreUser.findByEmail", email).firstResult()
    }

    fun createUser(user: LibreUser): Uni<LibreUser?> {
        return findByEmail(user.email)
            .onItem().ifNotNull().failWith(ValidationError(listOf(ErrorDescription("email", "A User with this E-Mail already exists."))))
            .onItem().ifNull().continueWith(user)
            .invoke(Unchecked.consumer { new ->
                if (new!!.role == null) new.role = "User"

                val violations = validator.validate(new)

                if (violations.isNotEmpty()) {
                    val errors = violations.map { violation ->
                        ErrorDescription(violation.propertyPath.filterNotNull()[0].name, violation.message)
                    }

                    throw ValidationError(errors)
                }
            }).chain { new -> persistAndFlush(new!!) }
    }

    @WithTransaction
    fun findByEmailAndPassword(email: String, password: String): Uni<LibreUser?> =
        findByEmail(email).onItem().ifNotNull().invoke (Unchecked.consumer { user ->
            if (!BcryptUtil.matches(password, user!!.password)) {
                throw EntityNotFoundException()
            }
        }).onItem().ifNull().failWith(EntityNotFoundException())

    /**
     * Update 2 fields only: avatar, name
     */
    @WithTransaction
    fun updateUser(libreUser: LibreUser, userId: UUID): Uni<LibreUser?> {
        return findByEmailAndPassword(libreUser.email, libreUser.password).map (Unchecked.function { user ->

            if (user!!.id != userId) {
                throw UnauthorizedException()
            }

            user
        }).call{ user ->
            user!!.avatar = libreUser.avatar
            user.name = libreUser.name

            Panache.getSession().call { s -> s.merge(user)}
        }
    }
}
