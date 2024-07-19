package io.tohuwabohu.security

import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.quarkus.security.identity.request.TrustedAuthenticationRequest
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.LibreUserRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class TrustedLibreUserIdentityProviderTest {

    private val libreUserRepository: LibreUserRepository = mockk()
    private val identityProvider = TrustedLibreUserIdentityProvider(libreUserRepository)

    @Test
    fun `test authenticate with valid user id`() = runBlocking {
        val mockUser = LibreUser(
            id = UUID.randomUUID(),
            email = "test@test.com",
            password = "<pwd>",
            role = "USER"
        )

        val request = TrustedAuthenticationRequest(mockUser.id.toString())
        coEvery { libreUserRepository.findById(mockUser.id!!) } returns Uni.createFrom().item(mockUser)

        val identity = identityProvider.authenticate(request, mockk()).await().indefinitely()

        assertEquals(mockUser.id.toString(), identity.principal.name)
        assertTrue(identity.roles.contains(mockUser.role))
    }

    @Test
    fun `test authenticate with valid email`() = runBlocking {
        val mockUser = LibreUser(
            id = UUID.randomUUID(),
            email = "test@test.com",
            password = "<pwd>",
            role = "USER"
        )

        val request = TrustedAuthenticationRequest(mockUser.email!!)
        coEvery { libreUserRepository.findByEmail(mockUser.email!!) } returns Uni.createFrom().item(mockUser)

        val identity = identityProvider.authenticate(request, mockk()).await().indefinitely()

        assertEquals(mockUser.id.toString(), identity.principal.name)
        assertTrue(identity.roles.contains(mockUser.role))
    }

    @Test
    fun `test authenticate with invalid id or email`() = runBlocking {
        val request = TrustedAuthenticationRequest("not@existing.mail")
        coEvery { libreUserRepository.findById(any()) } returns Uni.createFrom().nullItem()
        coEvery { libreUserRepository.findByEmail(any()) } returns Uni.createFrom().nullItem()

        val identity = identityProvider.authenticate(request, mockk()).await().indefinitely()

        assertTrue(identity.isAnonymous)
    }
}