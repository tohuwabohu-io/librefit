package io.tohuwabohu.security

import io.quarkus.logging.Log
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.TrustedAuthenticationRequest
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.jpa.reactive.runtime.JpaReactiveTrustedIdentityProvider
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.LibreUserRepository
import jakarta.enterprise.context.ApplicationScoped
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*

@ApplicationScoped
class LibreUserIdentityProvider(private val libreUserRepository: LibreUserRepository) :
    IdentityProvider<UsernamePasswordAuthenticationRequest> {
    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> {
        return UsernamePasswordAuthenticationRequest::class.java
    }

    override fun authenticate(
        request: UsernamePasswordAuthenticationRequest,
        authenticationRequestContext: AuthenticationRequestContext
    ): Uni<SecurityIdentity> {
        return libreUserRepository.findByEmailAndPassword(request.username, request.password.password.joinToString(""))
            .onItem().transform { libreUser ->
                QuarkusSecurityIdentity.builder()
                    .setPrincipal(QuarkusPrincipal(libreUser!!.id.toString()))
                    .addRole(libreUser.role)
                    .setAnonymous(false)
                    .build() as SecurityIdentity
            }.onFailure().invoke { e -> Log.error(e) }
            .onFailure()
            .recoverWithItem(QuarkusSecurityIdentity.builder().setAnonymous(true).build() as SecurityIdentity)
    }

    override fun priority(): Int {
        return super.priority() + 2
    }
}

@ApplicationScoped
class TrustedLibreUserIdentityProvider(private val libreUserRepository: LibreUserRepository) : JpaReactiveTrustedIdentityProvider() {
    override fun authenticate(session: Mutiny.Session, request: TrustedAuthenticationRequest): Uni<SecurityIdentity> {
        return libreUserRepository.findById(UUID.fromString(request.principal)).onItem().transform { libreUser ->
            QuarkusSecurityIdentity.builder()
                .setPrincipal(QuarkusPrincipal(libreUser!!.id.toString()))
                .addRole(libreUser.role)
                .setAnonymous(false)
                .build() as SecurityIdentity
        }.onFailure().invoke { e -> Log.error(e) }
            .onFailure()
            .recoverWithItem(QuarkusSecurityIdentity.builder().setAnonymous(true).build() as SecurityIdentity)
    }

    override fun priority(): Int {
        return super.priority() + 1
    }
}
