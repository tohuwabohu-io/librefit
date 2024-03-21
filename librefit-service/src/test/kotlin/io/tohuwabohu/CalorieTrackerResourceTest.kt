package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
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
        Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
            body("sequence", equalTo(1))
        }
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create two entries`() {
        val createdEntry1 = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        val createdEntry2 = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        assert(createdEntry1.sequence != createdEntry2.sequence)
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
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and read an entry`() {
        val createdEntry = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        val readEntry = When {
            get("/read/${createdEntry.added}/${createdEntry.sequence}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        assert(createdEntry.added == readEntry.added)
        assert(createdEntry.sequence == readEntry.sequence)
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

        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(entry)
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        created.amount = 200f
        created.category = Category.LUNCH

        val updated = Given {
            header("Content-Type", ContentType.JSON)
            body(created)
        } When {
            put("/update")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        assert(created.sequence == updated.sequence)
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
        Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("e27c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            put("/update")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }

        When {
            delete("/delete/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        }
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

        When {
            delete("/delete/$calorieTrackerId")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should create and delete an entry and fail on read`() {
        val createdEntry = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(CalorieTrackerEntry::class.java)
        }


        When {
            delete("/delete/${createdEntry.added}/${createdEntry.sequence}")
        } Then {
            statusCode(200)
        }

        When {
            get("/read/${createdEntry.added}/${createdEntry.sequence}")
        } Then {
            statusCode(404)
        }
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

        listOf(entry1, entry2, entry3).forEach { entry ->
            Given {
                header("Content-Type", ContentType.JSON)
                body(entry)
            } When {
                post("/create")
            } Then {
                statusCode(201)
            }
        }

        val dates = Given {
            header("Content-Type", ContentType.JSON)
        } When {
            get("/list/dates")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Array<LocalDate>::class.java)
        }

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
            Given {
                header("Content-Type", ContentType.JSON)
                body(entry)
            } When {
                post("/create")
            } Then {
                statusCode(201)
            }
        }

        val entries = When {
            get("/list/$added")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Array<CalorieTrackerEntry>::class.java)
        }

        assert(entries.size == 2)
        assert(entries.map { it.sequence }.isNotEmpty())
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should fail with 404`() {
        val userId = UUID.fromString("85b6a2e4-7fb3-11ee-b962-0242ac120002") // unrelated user's data

        val entry = entry(userId)

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
    @TestSecurity(user = "f31a553b-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create four entries with ascending identifiers`() {
        for (i in 1..4) {
            Given {
                header("Content-Type", ContentType.JSON)
                body(entry(UUID.fromString("f31a553b-7fb2-11ee-b962-0242ac120002")))
            } When {
                post("/create")
            } Then {
                statusCode(201)
                body("sequence", equalTo(i))
            }
        }
    }

    @Test
    @TestSecurity(user = "69f01b4e-7fb3-11ee-b962-0242ac120003", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test3@libre.fit"),
        ]
    )
    fun `should create two entries and list with range`() {
        val userId = UUID.fromString("69f01b4e-7fb3-11ee-b962-0242ac120003")

        val entry1 = entry(userId)
        entry1.added = LocalDate.of(2024, 1, 3)

        val entry2 = entry(userId)
        entry2.added = LocalDate.of(2024, 1, 31)

        listOf(entry1, entry2).forEach { entry ->
            Given {
                header("Content-Type", ContentType.JSON)
                body(entry)
            } When {
                post("/create")
            } Then {
                statusCode(201)
            }
        }

        val entries = When {
            get("/list/${entry1.added}/${entry2.added}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Array<CalorieTrackerEntry>::class.java)
        }

        assert(entries.size == 2)
        assert(entries.map { it.sequence }.isNotEmpty())
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