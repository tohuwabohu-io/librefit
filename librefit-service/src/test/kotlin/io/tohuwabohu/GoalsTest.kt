package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.tohuwabohu.crud.Goal
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(GoalsResource::class)
class GoalsTest {
    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create an entry`() {
        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
            .post("/create")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create two entries`() {
        val created1 = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
            .post("/create")
            .then()

        created1.assertThat().statusCode(201)

        val createdEntry1 = created1.extract().body().`as`(Goal::class.java)

        val created2 = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
            .post("/create")
            .then()

        created2.assertThat().statusCode(201)

        val createdEntry2 = created2.extract().body().`as`(Goal::class.java)

        assert(createdEntry1.id != createdEntry2.id)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on creation`() {
        var faultyEntry = goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002"))

        faultyEntry.startAmount = -100f

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(faultyEntry)
            .post("/create")
            .then()
            .assertThat()
            .statusCode(400)

        faultyEntry = goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002"))
        faultyEntry.endAmount = -200f

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(faultyEntry)
            .post("/create")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and read an entry`() {
        val created = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
            .post("/create")
            .then()

        created.assertThat().statusCode(201)

        val createdEntry = created.extract().body().`as`(Goal::class.java)

        val read = RestAssured.given().get("/read/${createdEntry.added}/${createdEntry.id}")
            .then()

        read.assertThat().statusCode(200)

        val readEntry = read.extract().body().`as`(Goal::class.java)

        assert(createdEntry.added == readEntry.added)
        assert(createdEntry.id == readEntry.id)
        assert(createdEntry.userId == readEntry.userId)
        assert(createdEntry.startAmount == readEntry.startAmount)
        assert(createdEntry.endAmount == readEntry.endAmount)
        assert(createdEntry.startDate == readEntry.startDate)
        assert(createdEntry.endDate == readEntry.endDate)
    }

    @Test
    @TestSecurity(user = "410a1496-7fc7-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create, update and read an entry`() {
        val entry = goal(userId = UUID.fromString("410a1496-7fc7-11ee-b962-0242ac120002"))

        val assured = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .post("/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(Goal::class.java)
        created.endAmount = 80.4f

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(created)
            .put("/update")
            .then()
            .assertThat()
            .statusCode(200)

        val assuredRead = RestAssured.given().get("/read/${created.added}/${created.id}").then()

        assuredRead.assertThat().statusCode(200)

        val updated = assuredRead.extract().body().`as`(Goal::class.java)

        assert(created.id == updated.id)
        assert(entry.endAmount != updated.endAmount)
        assert(updated.updated != null)
    }

    @Test
    @TestSecurity(user = "9f93b4fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on update`() {
        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(userId = UUID.fromString("9f93b4fc-7fb3-11ee-b962-0242ac120002")))
            .put("/update")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry`() {
        val assured = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
            .post("/create")
            .then()

        assured.assertThat().statusCode(201)

        val created = assured.extract().body().`as`(Goal::class.java)

        RestAssured.given().delete("/delete/${created.added}/${created.id}").then().assertThat().statusCode(200)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on delete`() {
        val calorieTrackerId = 123L

        RestAssured.given().delete("/delete/$calorieTrackerId").then().assertThat().statusCode(404)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry and fail on read`() {
        val assured = RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
            .post("/create")
            .then()
            .assertThat().statusCode(201)

        val createdEntry = assured.extract().body().`as`(Goal::class.java)

        RestAssured.given()
            .delete("/delete/${createdEntry.added}/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(200)

        RestAssured.given()
            .get("/read/${createdEntry.added}/${createdEntry.id}")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    @TestSecurity(user = "1171b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on finding last entry`() {
        RestAssured
            .given()
            .get("/last")
            .then().assertThat().statusCode(404)
    }

    @Test
    @TestSecurity(user = "07225bfc-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create two entries and find the last`() {
        val goal = goal(UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002"))
        goal.startAmount = 100f
        goal.endAmount = 200f

        val lastGoal = goal(UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002"))
        lastGoal.startAmount = 50f
        lastGoal.endAmount = 250f

        listOf(goal, lastGoal).forEach { item ->
            RestAssured.given()
                .header("Content-Type", ContentType.JSON)
                .body(item)
                .post("/create")
                .then()
                .assertThat()
                .statusCode(201)
        }

        val assured = RestAssured.given().get("/last").then()
        val found = assured.extract().body().`as`(Goal::class.java)

        assert(found.added == lastGoal.added)
        assert(found.userId == lastGoal.userId)
        assert(found.startAmount == lastGoal.startAmount)
        assert(found.endAmount == lastGoal.endAmount)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail with 401`() {
        val userId = UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002") // unrelated user's data

        val entry = goal(userId)

        RestAssured.given()
            .header("Content-Type", ContentType.JSON)
            .body(entry)
            .put("/update")
            .then()
            .assertThat()
            .statusCode(401)
    }

    private fun goal(userId: UUID): Goal {
        val goal = Goal(
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusYears(1),
            startAmount = 95.3f,
            endAmount = 75.4f
        )

        goal.userId = userId
        goal.added = LocalDate.now()

        return goal
    }
}