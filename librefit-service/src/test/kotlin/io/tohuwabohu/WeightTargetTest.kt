package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.crud.WeightTarget
import io.tohuwabohu.crud.error.ErrorDescription
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(WeightTargetResource::class)
class WeightTargetTest {
    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create an entry`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(weightTarget(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on creation`() {
        var faultyEntry = weightTarget(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002"))
        faultyEntry.targetWeight = -100f

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
    fun `should create, update and read an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(weightTarget(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTarget::class.java)
        }

        created.targetWeight = 80.4f

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
            body().`as`(WeightTarget::class.java)
        }

        assert(created.sequence == read.sequence)
        assert(created.targetWeight == read.targetWeight)
    }

    @Test
    @TestSecurity(user = "9f93b4fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on update`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(weightTarget(userId = UUID.fromString("9f93b4fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            put("/update")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "9f93b4fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create, delete and check non-existence of an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(weightTarget(userId = UUID.fromString("9f93b4fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTarget::class.java)
        }

        // delete entry
        When {
            delete("/delete/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        }

        // check if entry was deleted
        When {
            get("/read/${created.added}/${created.sequence}")
        } Then {
            statusCode(404)
        }
    }

    // ... continuing from the previous test

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create two entries`() {
        val created1 = Given {
            header("Content-Type", ContentType.JSON)
            body(weightTarget(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTarget::class.java)
        }

        val created2 = Given {
            header("Content-Type", ContentType.JSON)
            body(weightTarget(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTarget::class.java)
        }

        assert(created1.sequence != created2.sequence)
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
        val weightTarget1 = weightTarget(UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002"))
        weightTarget1.targetWeight = 100f

        val weightTarget2 = weightTarget(UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002"))
        weightTarget2.targetWeight = 50f

        listOf(weightTarget1, weightTarget2).forEach { item ->
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
            body("added", equalTo(weightTarget2.added.toString()))
            body("userId", equalTo(weightTarget2.userId.toString()))
            body("targetWeight", equalTo(weightTarget2.targetWeight))
        }
    }

    @Test
    @TestSecurity(user = "9f93c5fc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail with 401`() {
        val userId = UUID.fromString("07225bfc-7fb4-11ee-b962-0242ac120002") // unrelated user's data

        val entry = weightTarget(userId)

        Given {
            header("Content-Type", ContentType.JSON)
            body(entry)
        } When {
            put("/update")
        } Then {
            statusCode(404)
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
            body(weightTarget(userId = UUID.fromString("9f93c5fc-7fb3-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTarget::class.java)
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

    private fun weightTarget(userId: UUID): WeightTarget {
        val target = WeightTarget(
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusYears(1),
            initialWeight = 1921f,
            targetWeight = 2200f
        )

        target.userId = userId
        target.added = LocalDate.now()

        return target
    }
}