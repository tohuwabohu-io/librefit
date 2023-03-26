package io.tohuwabohu

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.Category
import org.junit.jupiter.api.Test
import java.time.LocalDate

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
    fun `should create two entries`() {
        val created1 = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry())
            .post("/tracker/calories/create")
            .then()

        created1.assertThat().statusCode(201)

        val createdEntry1 = created1.extract().body().`as`(CalorieTrackerEntry::class.java)

        val created2 = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry())
            .post("/tracker/calories/create")
            .then()

        created2.assertThat().statusCode(201)

        val createdEntry2 = created2.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(createdEntry1.id != null)
        assert(createdEntry2.id != null)
        assert(createdEntry1.id != createdEntry2.id)
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
    fun `should create and read an entry`() {
        val entry = entry()

        val created = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .post("/tracker/calories/create")
            .then()

        created.assertThat().statusCode(201)

        val createdEntry = created.extract().body().`as`(CalorieTrackerEntry::class.java)

        val read = given().get("/tracker/calories/read/${createdEntry.id}")
            .then()

        read.assertThat().statusCode(200)

        val readEntry = read.extract().body().`as`(CalorieTrackerEntry::class.java)

        println(createdEntry)
        println(readEntry)

        assert(createdEntry.id == readEntry.id)
        assert(createdEntry.amount == readEntry.amount)
        assert(createdEntry.category == readEntry.category)
        assert(createdEntry.userId == readEntry.userId)
    }

    @Test
    fun `should create, update and read an entry`() {
        val entry = entry()

        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .post("/tracker/calories/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(created.id != null)

        created.amount = 200f
        created.category = Category.LUNCH

        given()
            .header("Content-Type", ContentType.JSON)
            .body(created)
            .put("/tracker/calories/update")
            .then()
            .assertThat()
            .statusCode(200)

        val assuredRead = given().get("/tracker/calories/read/${created.id}").then()

        assuredRead.assertThat().statusCode(200)

        val updated = assuredRead.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(created.id == updated.id)
        assert(entry.amount != updated.amount)
        assert(entry.category != updated.category)
        assert(updated.updated != null)
    }

    @Test
    fun `should fail on update`() {
        val entry = entry()
        entry.id = 43L

        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .put("/tracker/calories/update")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    fun `should create and delete an entry`() {
        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry())
            .post("/tracker/calories/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(CalorieTrackerEntry::class.java)

        given().delete("/tracker/calories/delete/${created.id}").then().assertThat().statusCode(200)
    }

    @Test
    fun `should fail on delete`() {
        val calorieTrackerId = 123L

        given().delete("/tracker/calories/delete/$calorieTrackerId").then().assertThat().statusCode(404)
    }

    @Test
    fun `should create and delete an entry and fail on read`() {
        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry())
            .post("/tracker/calories/create")
            .then()
            .assertThat().statusCode(201)

        val createdEntry = assured.extract().body().`as`(CalorieTrackerEntry::class.java)

        given()
            .delete("/tracker/calories/delete/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(200)

        given()
            .get("/tracker/calories/read/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    fun `should create three entries and return two dates`() {
        val userId = 42L

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val entry1 = entry()
        entry1.userId = userId
        entry1.added = today

        val entry2 = entry()
        entry2.userId = userId
        entry2.added = today

        val entry3 = entry()
        entry3.userId = userId
        entry3.added = yesterday

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

    @Test
    fun `should create two entries and list them`() {
        val userId = 17L

        val entry1 = entry()
        entry1.userId = userId

        val entry2 = entry()
        entry2.userId = userId

        val added = entry1.added

        listOf(entry1, entry2).forEach { entry ->
            given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/tracker/calories/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val body = given().get("/tracker/calories/list/$userId/$added").body

        val entries = body.`as`(Array<CalorieTrackerEntry>::class.java)

        assert(entries.size == 2)
        assert(entries.map { it.id }.isNotEmpty())
    }

    private fun entry(): CalorieTrackerEntry {
        val entry = CalorieTrackerEntry()
        entry.amount = 100f
        entry.category = Category.SNACK
        entry.added = LocalDate.now()
        entry.userId = 1

        return entry;
    }
}