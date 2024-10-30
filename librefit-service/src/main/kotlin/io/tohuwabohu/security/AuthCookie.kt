package io.tohuwabohu.security

import io.quarkus.vertx.http.runtime.FormAuthRuntimeConfig
import jakarta.ws.rs.core.NewCookie

fun convertSameSite(sameSite: FormAuthRuntimeConfig.CookieSameSite): NewCookie.SameSite {
    return when (sameSite) {
        FormAuthRuntimeConfig.CookieSameSite.STRICT -> NewCookie.SameSite.STRICT
        FormAuthRuntimeConfig.CookieSameSite.LAX -> NewCookie.SameSite.LAX
        else -> NewCookie.SameSite.NONE
    }
}