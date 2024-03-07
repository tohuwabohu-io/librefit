package io.tohuwabohu.crud

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.security.ForbiddenException
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.unchecked.Unchecked
import io.tohuwabohu.crud.relation.LibreUserRelatedRepository
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.time.LocalDateTime
import java.util.*

@Entity
@NamedQueries(
    NamedQuery(name = "AuthSession.findRefreshToken",
        query = "from AuthSession where refreshToken = ?1"
    ),
    NamedQuery(name = "AuthSession.listRefreshTokens",
        query = "from AuthSession where userId = ?1 order by added desc"
    )
)
@Cacheable
data class AuthSession (
    @Column(nullable = false, unique = true)
    var refreshToken: String? = null,

    var expiresAt: LocalDateTime? = null
): LibreUserWeakEntity() {
    @PrePersist
    fun preInsert() {
        refreshToken = BcryptUtil.bcryptHash(refreshToken)
    }
}

@ApplicationScoped
class AuthRepository : LibreUserRelatedRepository<AuthSession>() {
    @ConfigProperty(name = "libreuser.tokens.max", defaultValue = "1")
    private lateinit var maxTokens: String

    fun findSession(token: String): Uni<AuthSession?> = find("#AuthSession.findRefreshToken", token)
        .firstResult().onItem().ifNull().failWith(ForbiddenException("Refresh token expired."))
        .onItem().ifNotNull().invoke (Unchecked.consumer { authSession ->
            if (authSession!!.expiresAt!!.isBefore(LocalDateTime.now())) throw ForbiddenException("Refresh token expired.")
        })

    fun addSession(userId: UUID, access: Pair<String, LocalDateTime>, refresh: Pair<String, LocalDateTime>): Uni<AuthInfo> = list("#AuthSession.listRefreshTokens", userId)
        .onItem().ifNotNull().transform { list ->
            if (list.size >= maxTokens.toLong()) list[0]
            else null
        }.chain { oldest ->
            val authSession = AuthSession(refreshToken = refresh.first, expiresAt = refresh.second)
            authSession.userId = userId

            oldest?.let { deleteEntry(oldest.getPrimaryKey()) }?.chain { _ ->
                validateAndPersist(authSession)
            } ?: validateAndPersist(authSession)
        }.onItem().transform { persistedAuthSession ->
            AuthInfo(
                accessToken = access.first,
                accessExpires = access.second,
                refreshToken = persistedAuthSession.refreshToken!!,
                refreshExpires = refresh.second
            )
        }

    fun invalidateSession(refreshToken: String): Uni<Boolean> = findSession(refreshToken)
        .onItem().ifNotNull().transform { authSession -> authSession!!.getPrimaryKey() }.chain{key -> deleteEntry(key) }

}

class AuthInfo (val accessToken: String, val accessExpires: LocalDateTime? = null, val refreshToken: String, val refreshExpires: LocalDateTime? = null)