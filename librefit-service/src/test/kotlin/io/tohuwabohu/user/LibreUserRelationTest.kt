package io.tohuwabohu.user

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.quarkus.security.identity.SecurityIdentity
import io.tohuwabohu.crud.CalorieTarget
import io.tohuwabohu.crud.CalorieTracker
import io.tohuwabohu.crud.WeightTarget
import io.tohuwabohu.crud.WeightTracker
import io.tohuwabohu.crud.user.LibreUserSecurity
import io.tohuwabohu.crud.user.LibreUserWeakEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.security.Principal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class LibreUserRelationTest {
    @MockK
    private lateinit var mockSecurityIdentity: SecurityIdentity

    @Test
    fun `test withPrincipal`() {
        val uuidString = "123e4567-e89b-12d3-a456-426655440000"
        val expectedUserId = UUID.fromString(uuidString)

        every { mockSecurityIdentity.principal } returns Principal { uuidString }

        val entities: List<LibreUserWeakEntity> = listOf(
            CalorieTracker(amount = 500f, category = "l"),
            CalorieTarget(startDate = LocalDate.of(2024,5,1), endDate = LocalDate.of(2025,5,1), targetCalories = 1800f, maximumCalories = 2300f),
            WeightTracker(amount = 75f),
            WeightTarget(startDate = LocalDate.of(2024,5,1), endDate = LocalDate.of(2025,5,1), initialWeight = 90f, targetWeight = 73f)
        )

        entities.forEach { entity ->
            LibreUserSecurity.withPrincipal(mockSecurityIdentity, entity)
            assertEquals(expectedUserId, entity.userId)
        }
    }
}
