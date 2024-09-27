package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.crud.CalorieTarget
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(CalorieTargetResource::class)
class CalorieTargetResourceTest {

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create an entry`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create two entries`() {
        val created1 = Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        val created2 = Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        assert(created1.sequence != created2.sequence)
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on creation`() {
        var faultyEntry = calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002"))
        faultyEntry.targetCalories = -100f

        Given {
            header("Content-Type", ContentType.JSON)
            body(faultyEntry)
        } When {
            post("/create")
        } Then {
            statusCode(400)
        }

        faultyEntry = calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002"))
        faultyEntry.maximumCalories = -200f

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
    fun `should create and read an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        val read = When {
            get("/read/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        assert(created.added == read.added)
        assert(created.sequence == read.sequence)
        assert(created.userId == read.userId)
        assert(created.startDate == read.startDate)
        assert(created.endDate == read.endDate)
    }

    @Test
    @TestSecurity(user = "410a1496-7fc7-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create, update and read an entry`() {
        val calorieTarget = calorieTargetResource(userId = UUID.fromString("410a1496-7fc7-11ee-b962-0242ac120002"))

        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTarget)
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        created.targetCalories = 80.4f

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
            body().`as`(CalorieTarget::class.java)
        }

        assert(created.sequence == read.sequence)
        assert(created.targetCalories == read.targetCalories)
        assert(read.updated != null)
    }

    @Test
    @TestSecurity(user = "9f93b4fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on update`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93b4fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            put("/update")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create and delete an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        When {
            delete("/delete/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on delete`() {
        val targetId = 123L

        When {
            delete("/delete/${targetId}")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create and delete an entry and fail on read`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(calorieTargetResource(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTarget::class.java)
        }

        When {
            delete("/delete/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        }

        When {
            get("/read/${created.added}/${created.sequence}")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "1171b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on finding last entry`() {
        When {
            get("/last")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "07225bfc-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create two entries and find the last`() {
        val calorieTarget1 = calorieTargetResource(UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002"))
        calorieTarget1.targetCalories = 100f
        calorieTarget1.maximumCalories = 200f

        val calorieTarget2 = calorieTargetResource(UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002"))
        calorieTarget2.targetCalories = 50f
        calorieTarget2.maximumCalories = 250f

        listOf(calorieTarget1, calorieTarget2).forEach { item ->
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
            body("added", equalTo(calorieTarget2.added.toString()))
            body("userId", equalTo(calorieTarget2.userId.toString()))
            body("targetCalories", equalTo(calorieTarget2.targetCalories))
            body("maximumCalories", equalTo(calorieTarget2.maximumCalories))
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail with 401`() {
        val userId = UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002") // unrelated user's data

        val entry = calorieTargetResource(userId)

        Given {
            header("Content-Type", ContentType.JSON)
            body(entry)
        } When {
            put("/update")
        } Then {
            statusCode(404)
        }
    }

    private fun calorieTargetResource(userId: UUID): CalorieTarget {
        val target = CalorieTarget(
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusYears(1),
            targetCalories = 1921f,
            maximumCalories = 2200f
        )

        target.userId = userId
        target.added = LocalDate.now()

        return target
    }
}