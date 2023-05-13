package io.tohuwabohu.security

import io.quarkus.logging.Log
import io.quarkus.security.UnauthorizedException
import io.smallrye.jwt.build.Jwt
import io.tohuwabohu.crud.LibreUser
import org.eclipse.microprofile.jwt.Claim
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

    Log.info("Auth info: userPrincipal.name=$name isHttps=${ctx.isSecure} authScheme=${ctx.authenticationScheme} hasJwt=${jwt.claimNames != null}")
}

fun generateToken(user: LibreUser): String =
    Jwt.issuer("https://libre.fitness/")
        .upn(user.email)
        .claim(Claims.email, user.email)
        .claim(Claims.nickname, user.name)
        .claim("id", user.id)
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