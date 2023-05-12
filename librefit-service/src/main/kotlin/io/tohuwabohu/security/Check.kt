package io.tohuwabohu.security

import io.quarkus.logging.Log
import io.quarkus.security.UnauthorizedException
import org.eclipse.microprofile.jwt.JsonWebToken
import javax.ws.rs.core.SecurityContext

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