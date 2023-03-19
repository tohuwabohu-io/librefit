package io.tohuwabohu

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.Category
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.time.LocalDateTime

@TestMethodOrder(OrderAnnotation::class)
@QuarkusTest
class CalorieTrackerResourceTest {
    @Test
    @Order(1)
    fun `should create an entry`() {
        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry())
            .post("/tracker/calories/create")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    @Order(2)
    fun `should fail on creation`() {
        val faultyEntry = entry()
        faultyEntry.amount = -100f

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(faultyEntry)
            .post("/tracker/calories/create")
            .then()
            .assertThat()
            .statusCode(400)
    }

    private fun entry(): CalorieTrackerEntry {
        val entry = CalorieTrackerEntry()
        entry.amount = 100f
        entry.category = Category.SNACK
        entry.added = LocalDateTime.now()

        return entry;
    }
}