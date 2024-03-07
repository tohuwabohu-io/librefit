package io.tohuwabohu.security

import io.quarkus.logging.Log
import io.quarkus.security.UnauthorizedException
import io.smallrye.jwt.build.Jwt
import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun printAuthenticationInfo(jwt: JsonWebToken, ctx: SecurityContext) {
    val name = if (ctx.userPrincipal == null) {
        "anonymous"
    } else if (!ctx.userPrincipal.name.equals(jwt.name)) {
        throw UnauthorizedException("Principal and JsonWebToken names do not match")
    } else {
        ctx.userPrincipal.name
    }

    Log.info("Auth info: userPrincipal.name=$name isHttps=${ctx.isSecure} authScheme=${ctx.authenticationScheme} hasJwt=${jwt.claimNames != null} userId=${jwt.name}")
}

/**
 * Check if currently logged-in user's id equals the user id for the data object to manipulate
 */
fun validateToken(jwt: JsonWebToken, data: LibreUserWeakEntity) {
    if (!jwt.name.equals(data.userId.toString())) {
        Log.info("Trying to manipulate object belonging to userId=${data.userId}")

        throw UnauthorizedException("Trying to access unrelated user data")
    }
}

fun generateAccessToken(user: LibreUser, ttlMinutes: Int): Pair<String, LocalDateTime> {
    val expiresAt = System.currentTimeMillis() / 1000 + (ttlMinutes * 60)
    val expirationDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresAt * 1000), ZoneId.systemDefault())

    val jwtString = Jwt.issuer("https://libre.fitness/")
        .upn(user.id.toString())
        .claim(Claims.email, user.email)
        .claim(Claims.nickname, user.name)
        .groups(user.role)
        .expiresAt(expiresAt)
        .sign()

    return Pair(jwtString, expirationDate)
}

fun generateRefreshToken(ttlMinutes: Int): Pair<String, LocalDateTime> {
    val expiresAt = System.currentTimeMillis() / 1000 + (ttlMinutes * 60)
    val expirationDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresAt * 1000), ZoneId.systemDefault())

    return Pair(Jwt.issuer("https://libre.fitness/").expiresAt(expiresAt).sign(),
        expirationDate)
}