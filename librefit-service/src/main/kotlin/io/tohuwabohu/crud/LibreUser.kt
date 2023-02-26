package io.tohuwabohu.crud

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.transaction.Transactional

@Entity
class LibreUser {
    @Id
    @GeneratedValue
    var id: Long? = null
    lateinit var name: String
    lateinit var password: String
    lateinit var email: String
    lateinit var registered: LocalDateTime
    lateinit var lastLogin: LocalDateTime
}

@ApplicationScoped
class LibreUserRepository : PanacheRepository<LibreUser> {
    fun findByEmailAndPassword(email: String, password: String) =
        find("email = ?1 and password = ?2", email, password).firstResult()

    @Transactional
    fun create(user: LibreUser) = persist(user)
}