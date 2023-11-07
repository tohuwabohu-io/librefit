package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.unchecked.Unchecked
import io.tohuwabohu.crud.error.ValidationError
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.*
import jakarta.validation.Validator
import jakarta.validation.constraints.NotEmpty
import org.hibernate.Hibernate
import java.time.LocalDateTime

@Entity
@Cacheable
data class LibreUser (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    var id: Long = 0L,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    @field:NotEmpty(message = "The provided password is empty.")
    var password: String,

    @Column(nullable = true)
    var name: String? = null,
    var registered: LocalDateTime? = null,
    var lastLogin: LocalDateTime? = null,

    @Column(nullable = true)
    var avatar: String? = null
): PanacheEntityBase {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as LibreUser

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , email = $email , password = $password , name = $name , registered = $registered , lastLogin = $lastLogin , avatar = $avatar )"
    }
}

@ApplicationScoped
class LibreUserRepository : PanacheRepository<LibreUser> {
    @Inject
    private lateinit var validator: Validator

    fun findByEmail(email: String): Uni<LibreUser?> = find("email = ?1", email).firstResult()

    fun createUser(user: LibreUser): Uni<LibreUser?> {
        return findByEmail(user.email)
            .onItem().ifNotNull().failWith(ValidationError(listOf("A User with this E-Mail already exists.")))
            .onItem().ifNull().continueWith(user)
            .invoke(Unchecked.consumer { new ->
                val violations = validator.validate(new)

                if (violations.isNotEmpty()) {
                    throw ValidationError(violations.map { violation -> violation.message })
                }
            }).chain { new -> persistAndFlush(new!!) }
    }

    fun findByEmailAndPassword(email: String, password: String): Uni<LibreUser?> =
        find("email = ?1 and password = crypt(?2, password)", email, password).firstResult()

    @WithTransaction
    fun updateUser(libreUser: LibreUser): Uni<LibreUser> {
        return Panache.getSession()
            .call { s -> s.find(LibreUser::class.java, libreUser.id)
                .onItem().ifNull().failWith(EntityNotFoundException())
                .replaceWith(findByEmailAndPassword(libreUser.email, libreUser.password))
                .onItem().ifNull().failWith(ValidationError(listOf("Invalid password.")))
                .onItem().ifNotNull().invoke(Unchecked.consumer { _ ->
                    val violations = validator.validate(libreUser)

                    if (violations.isNotEmpty()) {
                        throw ValidationError(violations.map { violation -> violation.message })
                    }
                })}
            .chain{ s -> s.merge(libreUser)}
    }
}
