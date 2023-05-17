package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.Category
import org.junit.jupiter.api.Test
import java.time.LocalDate

@QuarkusTest
@TestHTTPEndpoint(CalorieTrackerResource::class)
class CalorieTrackerResourceTest {
    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should create an entry`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 1, userId = 1))
            .post("/create")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should create two entries`() {
        val created1 = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 4, userId = 1))
            .post("/create")
            .then()

        created1.assertThat().statusCode(201)

        val createdEntry1 = created1.extract().body().`as`(CalorieTrackerEntry::class.java)

        val created2 = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(5, 1))
            .post("/create")
            .then()

        created2.assertThat().statusCode(201)

        val createdEntry2 = created2.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(createdEntry1.id != createdEntry2.id)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should fail on creation`() {
        val faultyEntry = entry(id = 1, userId = 1)
        faultyEntry.amount = -100f

        given()
            .header("Content-Type", ContentType.JSON)
            .body(faultyEntry)
            .post("/create")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should create and read an entry`() {
        val created = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 2, userId = 1))
            .post("/create")
            .then()

        created.assertThat().statusCode(201)

        val createdEntry = created.extract().body().`as`(CalorieTrackerEntry::class.java)

        val read = given().get("/read/${createdEntry.added}/${createdEntry.id}")
            .then()

        read.assertThat().statusCode(200)

        val readEntry = read.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(createdEntry.added == readEntry.added)
        assert(createdEntry.id == readEntry.id)
        assert(createdEntry.userId == readEntry.userId)
        assert(createdEntry.amount == readEntry.amount)
        assert(createdEntry.category == readEntry.category)
    }

    @Test
    @TestSecurity(user = "4", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "4")
        ]
    )
    fun `should create, update and read an entry`() {
        val entry = entry(id = 1, userId = 4)

        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .post("/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(CalorieTrackerEntry::class.java)
        created.amount = 200f
        created.category = Category.LUNCH

        given()
            .header("Content-Type", ContentType.JSON)
            .body(created)
            .put("/update")
            .then()
            .assertThat()
            .statusCode(200)

        val assuredRead = given().get("/read/${created.added}/${created.id}").then()

        assuredRead.assertThat().statusCode(200)

        val updated = assuredRead.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(created.id == updated.id)
        assert(entry.amount != updated.amount)
        assert(entry.category != updated.category)
        assert(updated.updated != null)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should fail on update`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 43L, userId = 1))
            .put("/update")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should create and delete an entry`() {
        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 1, userId = 1))
            .post("/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(CalorieTrackerEntry::class.java)

        given().delete("/delete/${created.added}/${created.id}").then().assertThat().statusCode(200)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should fail on delete`() {
        val calorieTrackerId = 123L

        given().delete("/delete/$calorieTrackerId").then().assertThat().statusCode(404)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "1")
        ]
    )
    fun `should create and delete an entry and fail on read`() {
        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(id = 3, userId = 1))
            .post("/create")
            .then()
            .assertThat().statusCode(201)

        val createdEntry = assured.extract().body().`as`(CalorieTrackerEntry::class.java)

        given()
            .delete("/delete/${createdEntry.added}/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(200)

        given()
            .get("/read/${createdEntry.added}/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    @TestSecurity(user = "42", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "42")
        ]
    )
    fun `should create three entries and return two dates`() {
        val userId = 42L

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val entry1 = entry(id = 1, userId)
        entry1.added = today

        val entry2 = entry(id = 2, userId)
        entry2.added = today

        val entry3 = entry(id = 3, userId)
        entry3.added = yesterday

        listOf(entry1, entry2, entry3).forEach { entry ->
            given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val body = given()
            .header("Content-Type", ContentType.JSON)
            .get("/list/dates").body

        val dates = body.`as`(Array<LocalDate>::class.java)

        assert(dates.contains(today))
        assert(dates.contains(yesterday))

        assert(dates.count { date -> date == today } == 1)
        assert(dates.count { date -> date == yesterday } == 1)
    }

    @Test
    @TestSecurity(user = "17", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "17")
        ]
    )
    fun `should create two entries and list them`() {
        val userId = 17L

        val entry1 = entry(id = 1, userId)
        val entry2 = entry(id = 2, userId)

        val added = entry1.added

        listOf(entry1, entry2).forEach { entry ->
            given()
                .header("Content-Type", ContentType.JSON)
                .body(entry)
                .post("/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val body = given().get("/list/$added").body

        val entries = body.`as`(Array<CalorieTrackerEntry>::class.java)

        assert(entries.size == 2)
        assert(entries.map { it.id }.isNotEmpty())
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
            Claim(key = "id", value = "17")
        ]
    )
    fun `should fail with 401`() {
        val userId = 2L // unrelated user's data

        val entry = entry(id = 1, userId)

        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .put("/update")
            .then()
            .assertThat()
            .statusCode(401)
    }

    private fun entry(id: Long, userId: Long): CalorieTrackerEntry {
        val entry = CalorieTrackerEntry(
            amount = 100f,
            category = Category.SNACK,
        )

        entry.id = id
        entry.userId = userId
        entry.added = LocalDate.now()

        return entry
    }
}