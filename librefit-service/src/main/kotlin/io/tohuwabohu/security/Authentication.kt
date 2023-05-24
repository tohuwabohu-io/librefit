package io.tohuwabohu.security

import io.quarkus.logging.Log
import io.quarkus.security.UnauthorizedException
import io.smallrye.jwt.build.Jwt
import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.relation.LibreUserWeakEntity
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken
import javax.ws.rs.core.SecurityContext

class AuthenticationResponse (val token: String)

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

fun generateToken(user: LibreUser): String =
    Jwt.issuer("https://libre.fitness/")
        .upn(user.id.toString())
        .claim(Claims.email, user.email)
        .claim(Claims.nickname, user.name)
        .groups(setOf("User"))
        .sign()

fun main() {
    println(generateToken(LibreUser(
        id = 1,
        email = "test@libre.fitness",
        name = "testuser",
        password = "1234"
    )))
}