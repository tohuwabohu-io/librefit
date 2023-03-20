package io.tohuwabohu

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.Category
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

@QuarkusTest
class CalorieTrackerResourceTest {
    @Test
    fun `should create an entry`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry())
            .post("/tracker/calories/create")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    fun `should fail on creation`() {
        val faultyEntry = entry()
        faultyEntry.amount = -100f

        given()
            .header("Content-Type", ContentType.JSON)
            .body(faultyEntry)
            .post("/tracker/calories/create")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    fun `should create three entries and return two dates`() {
        val userId = 42L

        val todayDt = LocalDateTime.now()
        val today = todayDt.toLocalDate()

        val yesterdayDt = todayDt.minusDays(1)
        val yesterday = todayDt.toLocalDate()

        val entry1 = entry()
        entry1.userId = userId
        entry1.added = todayDt

        val entry2 = entry()
        entry2.userId = userId
        entry2.added = todayDt

        val entry3 = entry()
        entry3.userId = userId
        entry3.added = yesterdayDt

        listOf(entry1, entry2, entry3).forEach { entry ->
            given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/tracker/calories/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val body = given()
            .header("Content-Type", ContentType.JSON)
            .get("/tracker/calories/list/$userId/dates").body

        val dates = body.`as`(Array<LocalDate>::class.java)

        assert(dates.contains(today))
        assert(dates.contains(yesterday))

        assert(dates.count { date -> date == today } == 1)
        assert(dates.count { date -> date == yesterday } == 1)
    }

    private fun entry(): CalorieTrackerEntry {
        val entry = CalorieTrackerEntry()
        entry.amount = 100f
        entry.category = Category.SNACK
        entry.added = LocalDateTime.now()
        entry.userId = 1

        return entry;
    }
}