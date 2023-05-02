package io.tohuwabohu

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.tohuwabohu.crud.WeightTrackerEntry
import org.junit.jupiter.api.Test
import java.time.LocalDate

@QuarkusTest
class WeightTrackerResourceTest {
    @Test
    fun `should create an entry`() {
        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 1, userId = 1))
            .post("/tracker/weight/create")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    fun `should create two entries`() {
        val created1 = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 4, userId = 1))
            .post("/tracker/weight/create")
            .then()

        created1.assertThat().statusCode(201)

        val createdEntry1 = created1.extract().body().`as`(WeightTrackerEntry::class.java)

        val created2 = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(5, 1))
            .post("/tracker/weight/create")
            .then()

        created2.assertThat().statusCode(201)

        val createdEntry2 = created2.extract().body().`as`(WeightTrackerEntry::class.java)

        assert(createdEntry1.id != createdEntry2.id)
    }

    @Test
    fun `should fail on creation`() {
        val faultyEntry = entry(id = 1, userId = 1)
        faultyEntry.amount = -100f

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(faultyEntry)
            .post("/tracker/weight/create")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    fun `should create and read an entry`() {
        val created = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 2, userId = 1))
            .post("/tracker/weight/create")
            .then()

        created.assertThat().statusCode(201)

        val createdEntry = created.extract().body().`as`(WeightTrackerEntry::class.java)

        val read = RestAssured.given().get("/tracker/weight/read/${createdEntry.userId}/${createdEntry.added}/${createdEntry.id}")
            .then()

        read.assertThat().statusCode(200)

        val readEntry = read.extract().body().`as`(WeightTrackerEntry::class.java)

        assert(createdEntry.added == readEntry.added)
        assert(createdEntry.id == readEntry.id)
        assert(createdEntry.userId == readEntry.userId)
        assert(createdEntry.amount == readEntry.amount)
    }

    @Test
    fun `should create, update and read an entry`() {
        val entry = entry(id = 1, userId = 4)

        val assured = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .post("/tracker/weight/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(WeightTrackerEntry::class.java)
        created.amount = 200f

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(created)
            .put("/tracker/weight/update")
            .then()
            .assertThat()
            .statusCode(200)

        val assuredRead = RestAssured.given().get("/tracker/weight/read/${created.userId}/${created.added}/${created.id}").then()

        assuredRead.assertThat().statusCode(200)

        val updated = assuredRead.extract().body().`as`(WeightTrackerEntry::class.java)

        assert(created.id == updated.id)
        assert(entry.amount != updated.amount)
        assert(updated.updated != null)
    }

    @Test
    fun `should fail on update`() {
        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 43L, userId = 1))
            .put("/tracker/weight/update")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    fun `should create and delete an entry`() {
        val assured = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 1, userId = 1))
            .post("/tracker/weight/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(WeightTrackerEntry::class.java)

        RestAssured.given().delete("/tracker/weight/delete/${created.userId}/${created.added}/${created.id}").then().assertThat().statusCode(200)
    }

    @Test
    fun `should fail on delete`() {
        val calorieTrackerId = 123L

        RestAssured.given().delete("/tracker/weight/delete/$calorieTrackerId").then().assertThat().statusCode(404)
    }

    @Test
    fun `should create and delete an entry and fail on read`() {
        val assured = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 3, userId = 1))
            .post("/tracker/weight/create")
            .then()
            .assertThat().statusCode(201)

        val createdEntry = assured.extract().body().`as`(WeightTrackerEntry::class.java)

        RestAssured.given()
            .delete("/tracker/weight/delete/${createdEntry.userId}/${createdEntry.added}/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(200)

        RestAssured.given()
            .get("/tracker/weight/read/${createdEntry.userId}/${createdEntry.added}/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    fun `should create two entries and list them`() {
        val userId = 17L

        val entry1 = entry(id = 1, userId)
        entry1.userId = userId

        val entry2 = entry(id = 2, userId)
        entry2.userId = userId

        val added = entry1.added

        listOf(entry1, entry2).forEach { entry ->
            RestAssured.given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/tracker/weight/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val body = RestAssured.given().get("/tracker/weight/list/$userId/$added").body

        val entries = body.`as`(Array<WeightTrackerEntry>::class.java)

        assert(entries.size == 2)
        assert(entries.map { it.id }.isNotEmpty())
    }

    @Test
    fun `should create three entries and list 2 in two date ranges`() {
        val userId = 18L
        val today = LocalDate.now()
        val lastWeek = today.minusWeeks(1)
        val lastMonth = today.minusMonths(1)

        val entry1 = entry(id = 1, userId)
        entry1.added = today

        val entry2 = entry(id = 1, userId)
        entry2.added = lastWeek

        val entry3 = entry(id = 1, userId)
        entry3.added = lastMonth

        listOf(entry1, entry2, entry3).forEach { entry ->
            RestAssured.given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/tracker/weight/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val range1Body = RestAssured.given().get("/tracker/weight/list/$userId/${entry2.added}/${entry1.added}").body
        val range2Body = RestAssured.given().get("/tracker/weight/list/$userId/${entry3.added}/${entry2.added}").body

        val range1Keys = range1Body.`as`(Array<WeightTrackerEntry>::class.java).map { it.getPrimaryKey() }
        val range2Keys = range2Body.`as`(Array<WeightTrackerEntry>::class.java).map { it.getPrimaryKey() }

        assert(range1Keys.containsAll(listOf(entry1.getPrimaryKey(), entry2.getPrimaryKey())))
        assert(!range1Keys.contains(entry3.getPrimaryKey()))

        assert(range2Keys.containsAll(listOf(entry2.getPrimaryKey(), entry3.getPrimaryKey())))
        assert(!range2Keys.contains(entry1.getPrimaryKey()))
    }

    @Test
    fun `should fail on finding last entry`() {
        val userId = 72L

        RestAssured
            .given()
            .get("/tracker/weight/last/$userId")
            .then().assertThat().statusCode(404)
    }

    @Test
    fun `should create three entries and find max added and id`() {
        val userId = 99L

        val lastWeek = LocalDate.now().minusWeeks(1)
        val lastMonth = LocalDate.now().minusMonths(1)

        val entry1 = entry(1, userId)
        entry1.added = lastWeek

        val entry2 = entry(2, userId)
        entry2.added = lastWeek

        val entry3 = entry(1, userId)
        entry3.added = lastMonth

        listOf(entry1, entry2, entry3).forEach { entry ->
            RestAssured.given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/tracker/weight/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val assured = RestAssured.given().get("/tracker/weight/last/$userId").then()

        assured.assertThat().statusCode(200)

        val lastEntry = assured.extract().body().`as`(WeightTrackerEntry::class.java)

        assert(lastEntry.getPrimaryKey() == entry2.getPrimaryKey())
    }

    private fun entry(id: Long, userId: Long): WeightTrackerEntry {
        val entry = WeightTrackerEntry(
            amount = 100f
        )

        entry.id = id
        entry.userId = userId
        entry.added = LocalDate.now()

        return entry
    }
}