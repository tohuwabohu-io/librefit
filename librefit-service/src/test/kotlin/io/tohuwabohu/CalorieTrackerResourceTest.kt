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
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(CalorieTrackerResource::class)
class CalorieTrackerResourceTest {
    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create an entry`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
            .post("/create")
            .then()
            .assertThat()
            .statusCode(201)
            .body("id", equalTo(1))
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create two entries`() {
        val created1 = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
            .post("/create")
            .then()

        created1.assertThat().statusCode(201)

        val createdEntry1 = created1.extract().body().`as`(CalorieTrackerEntry::class.java)

        val created2 = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
            .post("/create")
            .then()

        created2.assertThat().statusCode(201)

        val createdEntry2 = created2.extract().body().`as`(CalorieTrackerEntry::class.java)

        assert(createdEntry1.id != createdEntry2.id)
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on creation`() {
        val faultyEntry = entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002"))
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
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and read an entry`() {
        val created = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
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
    @TestSecurity(user = "3902536c-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create, update and read an entry`() {
        val entry = entry(userId = UUID.fromString("3902536c-7fb3-11ee-b962-0242ac120002"))

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
    @TestSecurity(user = "e27c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on update`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(userId = UUID.fromString("e27c313c-7fb2-11ee-b962-0242ac120002")))
            .put("/update")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry`() {
        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
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
        ]
    )
    fun `should fail on delete`() {
        val calorieTrackerId = 123L

        given().delete("/delete/$calorieTrackerId").then().assertThat().statusCode(404)
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry and fail on read`() {
        val assured = given()
            .header("Content-Type", ContentType.JSON)
            .body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
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
    @TestSecurity(user = "534662cc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create three entries and return two dates`() {
        val userId = UUID.fromString("534662cc-7fb3-11ee-b962-0242ac120002")

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val entry1 = entry(userId)
        entry1.added = today

        val entry2 = entry(userId)
        entry2.added = today

        val entry3 = entry(userId)
        entry3.added = yesterday

        listOf(entry1, entry2, entry3).forEachIndexed { i, entry ->
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
    @TestSecurity(user = "69f01b4e-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create two entries and list them`() {
        val userId = UUID.fromString("69f01b4e-7fb3-11ee-b962-0242ac120002")

        val entry1 = entry(userId)
        val entry2 = entry(userId)

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
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail with 401`() {
        val userId = UUID.fromString("85b6a2e4-7fb3-11ee-b962-0242ac120002") // unrelated user's data

        val entry = entry(userId)

        given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .put("/update")
            .then()
            .assertThat()
            .statusCode(401)
    }

    @Test
    @TestSecurity(user = "f31a553b-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create four entries with ascending identifiers`() {
        for (i in 1..4) {
            given()
                .header("Content-Type", ContentType.JSON)
                .body(entry(UUID.fromString("f31a553b-7fb2-11ee-b962-0242ac120002")))
                .post("/create")
                .then()
                .assertThat()
                .statusCode(201)
                .body("id", equalTo(i))
        }
    }

    private fun entry(userId: UUID): CalorieTrackerEntry {
        val entry = CalorieTrackerEntry(
            amount = 100f,
            category = Category.SNACK,
        )

        entry.userId = userId
        entry.added = LocalDate.now()

        return entry
    }
}