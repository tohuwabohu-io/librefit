package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.composite.Dashboard
import io.tohuwabohu.composite.Wizard
import io.tohuwabohu.crud.CalorieTarget
import io.tohuwabohu.crud.WeightTarget
import io.tohuwabohu.crud.WeightTracker
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(CompositeResource::class)
class CompositeResourceTest {
    @Test
    @TestSecurity(user = "2291b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should return dashboard data`() {
        val referenceDay = LocalDate.of(2024, 5, 31);

        val dashboard = When {
            get("/dashboard/${referenceDay}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Dashboard::class.java)
        }

        val userData = dashboard.userData;

        assert(userData.id == UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002"))
        assert(userData.name == "Sly")
        assert(userData.avatar == "/assets/rambo-1.jpg")

        val foodCategories = dashboard.foodCategories

        assert(foodCategories.isNotEmpty())

        val calorieTarget = dashboard.calorieTarget
        assert(calorieTarget!!.userId == UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002"))
        assert(calorieTarget.added == LocalDate.parse("2024-05-31"))
        assert(calorieTarget.sequence == 1L)
        assert(calorieTarget.targetCalories == 1900f)
        assert(calorieTarget.startDate == LocalDate.parse("2024-01-01"))
        assert(calorieTarget.endDate == LocalDate.parse("2024-12-31"))

        val caloriesToday = dashboard.caloriesTodayList

        assert(caloriesToday!!.size == 1)
        assert(caloriesToday[0].userId == UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002"))
        assert(caloriesToday[0].added == LocalDate.parse("2024-05-31"))
        assert(caloriesToday[0].sequence == 1L)
        assert(caloriesToday[0].amount == 300f)
        assert(caloriesToday[0].category == "l")

        // test data set has only one entry
        assert(caloriesToday == dashboard.caloriesWeekList);

        val weightTarget = dashboard.weightTarget
        assert(weightTarget!!.userId == UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002"))
        assert(weightTarget.initialWeight == 90f)
        assert(weightTarget.targetWeight == 75f)
        assert(weightTarget.added == LocalDate.parse("2024-05-01"))
        assert(weightTarget.startDate == LocalDate.parse("2024-01-01"))
        assert(weightTarget.endDate == LocalDate.parse("2024-12-31"))

        val weightMonthList = dashboard.weightMonthList
        assert(weightMonthList != null)
        assert(weightMonthList!!.size == 1)
        assert(weightMonthList[0].userId == UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002"))
        assert(weightMonthList[0].added == LocalDate.parse("2024-05-31"))
        assert(weightMonthList[0].sequence == 1L)
    }

    @Test
    @TestSecurity(user = "2291b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create result from the wizard`() {
        val baseDate = LocalDate.of(2024, 5, 1)

        val calorieTarget = CalorieTarget(
            startDate = baseDate,
            endDate = baseDate.plusYears(1),
            targetCalories = 2000f,
            maximumCalories = 2500f,
        ).apply {
            added = baseDate
            userId = UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002")
        }

        val weightTarget = WeightTarget(
            startDate = baseDate,
            endDate = baseDate.plusYears(1),
            initialWeight = 75f,
            targetWeight = 70f,
        ).apply {
            added = baseDate
            userId = UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002")
        }

        val weightTracker = WeightTracker(
            amount = 75f,
        ).apply {
            added = baseDate
            userId = UUID.fromString("2291b08c-7fb5-11ee-b962-0242ac120002")
        }

        val wizard = Wizard(calorieTarget, weightTarget, weightTracker);

        Given {
            header("Content-Type", ContentType.JSON)
            body(wizard)
        } When {
            post("/wizard/result")
        } Then {
            statusCode(201)
        }
    }
}