package io.tohuwabohu.crud

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.security.UnauthorizedException
import io.quarkus.security.jpa.*
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.unchecked.Unchecked
import io.tohuwabohu.crud.error.ErrorDescription
import io.tohuwabohu.crud.error.ValidationError
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.*
import jakarta.validation.Validator
import jakarta.validation.constraints.NotEmpty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime
import java.util.*

@Entity
@Cacheable
@UserDefinition
@NamedQueries(
    NamedQuery(name = "findByEmailAndPassword", query = "from LibreUser where email = ?1 and password = ?2")
)
data class LibreUser (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    var id: UUID? = null,

    @Username
    @Column(unique = true, nullable = false)
    var email: String,

    @Password(PasswordType.CLEAR) // <-- automatic encryption with annotation does not work due to a bug
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

    fun findByEmail(email: String): Uni<LibreUser?> = find("email = ?1", email).firstResult()

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
    fun updateUser(libreUser: LibreUser, jwt: JsonWebToken): Uni<LibreUser?> {
        return findByEmailAndPassword(libreUser.email, libreUser.password).map (Unchecked.function { user ->

            if (user!!.id != UUID.fromString(jwt.name)) {
                throw UnauthorizedException()
            }

            user
        }).call{ user ->
            user!!.avatar = libreUser.avatar
            user.name = libreUser.name

            Panache.getSession().call { s -> s.merge(user)}
        }
    }

    @WithTransaction
    fun activateUser(userId: UUID): Uni<LibreUser> {
        return findById(userId).call { user ->
            user.activated = true

            Panache.getSession().call { s -> s.merge(user) }
        }
    }
}
