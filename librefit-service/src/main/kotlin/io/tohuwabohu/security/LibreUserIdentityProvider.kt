package io.tohuwabohu.security

import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.quarkus.logging.Log
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.TrustedAuthenticationRequest
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.LibreUserRepository
import jakarta.annotation.Priority
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@Priority(IdentityProvider.SYSTEM_FIRST)
class TrustedLibreUserIdentityProvider(private val libreUserRepository: LibreUserRepository) :
    IdentityProvider<TrustedAuthenticationRequest> {

    override fun getRequestType(): Class<TrustedAuthenticationRequest> {
        return TrustedAuthenticationRequest::class.java
    }

    @WithSession
    override fun authenticate(
        request: TrustedAuthenticationRequest,
        context: AuthenticationRequestContext
    ): Uni<SecurityIdentity> {
        return libreUserRepository.findByEmail(request.principal).onItem().transform { libreUser ->
            QuarkusSecurityIdentity.builder()
                .setPrincipal(QuarkusPrincipal(libreUser!!.id.toString()))
                .addRole(libreUser.role)
                .setAnonymous(false)
                .build() as SecurityIdentity
        }.onFailure().invoke { e -> Log.error(e) }
            .onFailure()
            .recoverWithItem(QuarkusSecurityIdentity.builder().setAnonymous(true).build() as SecurityIdentity)
    }
}
