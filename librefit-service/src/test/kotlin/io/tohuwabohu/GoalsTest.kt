package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.crud.Goal
import org.hamcrest.Matchers.equalTo
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
        Given {
            header("Content-Type", ContentType.JSON)
            body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create two entries`() {
        val created1 = Given {
            header("Content-Type", ContentType.JSON)
            body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(Goal::class.java)
        }

        val created2 = Given {
            header("Content-Type", ContentType.JSON)
            body(goal(UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(Goal::class.java)
        }

        assert(created1.sequence != created2.sequence)
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

        Given {
            header("Content-Type", ContentType.JSON)
            body(faultyEntry)
        } When {
            post("/create")
        } Then {
            statusCode(400)
        }


        faultyEntry = goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002"))
        faultyEntry.endAmount = -200f

        Given {
            header("Content-Type", ContentType.JSON)
            body(faultyEntry)
        } When {
            post("/create")
        } Then {
            statusCode(400)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and read an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(Goal::class.java)
        }

        val read = When {
            get("/read/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Goal::class.java)
        }

        assert(created.added == read.added)
        assert(created.sequence == read.sequence)
        assert(created.userId == read.userId)
        assert(created.startAmount == read.startAmount)
        assert(created.endAmount == read.endAmount)
        assert(created.startDate == read.startDate)
        assert(created.endDate == read.endDate)
    }

    @Test
    @TestSecurity(user = "410a1496-7fc7-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create, update and read an entry`() {
        val goal = goal(userId = UUID.fromString("410a1496-7fc7-11ee-b962-0242ac120002"))

        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(goal)
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(Goal::class.java)
        }

        created.endAmount = 80.4f

        Given {
            header("Content-Type", ContentType.JSON)
            body(created)
        } When {
            put("/update")
        } Then {
            statusCode(200)
        }

        val read = When {
            get("/read/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Goal::class.java)
        }

        assert(created.sequence == read.sequence)
        assert(goal.endAmount != read.endAmount)
        assert(read.updated != null)
    }

    @Test
    @TestSecurity(user = "9f93b4fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on update`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(goal(userId = UUID.fromString("9f93b4fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            put("/update")
        } Then {
            statusCode(404)
        }

    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(Goal::class.java)
        }

        When {
            delete("/delete/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail on delete`() {
        val goalId = 123L

        When {
            delete("/delete/$goalId")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry and fail on read`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(goal(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(Goal::class.java)
        }

        RestAssured.given()
            .delete("/delete/${created.added}/${created.sequence}")
            .then()
            .assertThat()
            .statusCode(200)

        RestAssured.given()
            .get("/read/${created.added}/${created.sequence}")
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
        When {
            get("/last")
        } Then {
            statusCode(404)
        }
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
            Given {
                header("Content-Type", ContentType.JSON)
                body(item)
            } When {
                post("/create")
            } Then {
                statusCode(201)
            }
        }

        When {
            get("/last")
        } Then {
            statusCode(200)
            body("added", equalTo(lastGoal.added.toString()))
            body("userId", equalTo(lastGoal.userId.toString()))
            body("startAmount", equalTo(lastGoal.startAmount))
            body("endAmount", equalTo(lastGoal.endAmount))
        }
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

        Given {
            header("Content-Type", ContentType.JSON)
            body(entry)
        } When {
            put("/update")
        } Then {
            statusCode(401)
        }
    }

    private fun goal(userId: UUID): Goal {
        val goal = Goal(
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusYears(1),
            startAmount = 95.3f,
            endAmount = 75.4f,
            targetCalories = 1921f,
            maximumCalories = 2200f
        )

        goal.userId = userId
        goal.added = LocalDate.now()

        return goal
    }
}