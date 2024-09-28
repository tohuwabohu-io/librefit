package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.crud.WeightTracker
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(WeightTrackerResource::class)
class WeightTrackerResourceTest {
    @Test
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create an entry`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        }
    }

    @Test
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create two entries`() {
        val createdEntry1 = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        val createdEntry2 = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        assert(createdEntry1.sequence != createdEntry2.sequence)
    }

    @Test
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on creation`() {
        val faultyEntry = entry(userId = UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002"))
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
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create and read an entry`() {
        val createdEntry = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        val readEntry = When {
            get("/read/${createdEntry.added}/${createdEntry.sequence}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        assert(createdEntry.added == readEntry.added)
        assert(createdEntry.sequence == readEntry.sequence)
        assert(createdEntry.userId == readEntry.userId)
        assert(createdEntry.amount == readEntry.amount)
    }

    @Test
    @TestSecurity(user = "c9593830-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create, update and read an entry`() {
        val entry = entry(userId = UUID.fromString("c9593830-7fb4-11ee-b962-0242ac120002"))

        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(entry)
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        created.amount = 200f

        Given {
            header("Content-Type", ContentType.JSON)
            body(created)
        } When {
            put("/update")
        } Then {
            statusCode(200)
        }

        val readEntry = When {
            get("/read/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        assert(created.sequence == readEntry.sequence)
        assert(entry.amount != readEntry.amount)
        assert(readEntry.updated != null)
    }

    @Test
    @TestSecurity(user = "11f63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on update`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("11f63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            put("/update")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create and delete an entry`() {
        val created = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        When {
            delete("/delete/${created.added}/${created.sequence}")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on delete`() {
        val calorieTrackerId = 123L

        When {
            delete("/delete/$calorieTrackerId")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "71e63e90-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create and delete an entry and fail on read`() {
        val createdEntry = Given {
            header("Content-Type", ContentType.JSON)
            body(entry(userId = UUID.fromString("71e63e90-7fb4-11ee-b962-0242ac120002")))
        } When {
            post("/create")
        } Then {
            statusCode(201)
        } Extract {
            body().`as`(WeightTracker::class.java)
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
    @TestSecurity(user = "d6d8be4a-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create two entries and list them`() {
        val userId = UUID.fromString("d6d8be4a-7fb4-11ee-b962-0242ac120002")

        val entry1 = entry(userId)
        entry1.userId = userId

        val entry2 = entry(userId)
        entry2.userId = userId

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
            body().`as`(Array<WeightTracker>::class.java)
        }

        assert(entries.size == 2)
        assert(entries.map { it.sequence }.isNotEmpty())
    }

    @Test
    @TestSecurity(user = "e12d806a-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create three entries and list 2 in two date ranges`() {
        val userId = UUID.fromString("e12d806a-7fb4-11ee-b962-0242ac120002")
        val today = LocalDate.now()
        val lastWeek = today.minusWeeks(1)
        val lastMonth = today.minusMonths(1)

        val entry1 = entry(userId)
        entry1.added = today

        val entry2 = entry(userId)
        entry2.added = lastWeek

        val entry3 = entry(userId)
        entry3.added = lastMonth

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

        val entries1 = When {
            get("/list/${entry2.added}/${entry1.added}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Array<WeightTracker>::class.java)
        }

        val entries2 = When {
            get("/list/${entry3.added}/${entry2.added}")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(Array<WeightTracker>::class.java)
        }

        val range1Keys = entries1.map { it.getPrimaryKey() }
        val range2Keys = entries2.map { it.getPrimaryKey() }

        assert(range1Keys.containsAll(listOf(entry1.getPrimaryKey(), entry2.getPrimaryKey())))
        assert(!range1Keys.contains(entry3.getPrimaryKey()))

        assert(range2Keys.containsAll(listOf(entry2.getPrimaryKey(), entry3.getPrimaryKey())))
        assert(!range2Keys.contains(entry1.getPrimaryKey()))
    }

    @Test
    @TestSecurity(user = "534662cc-7fb3-11ee-b962-0242ac120002", roles = ["User"])
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
            get("/list/dates/$yesterday/$today")
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
    @TestSecurity(user = "410a1496-7fc7-11ee-b962-0242ac120002", roles = ["User"])
    fun `should fail on finding last entry`() {
        When {
            get("/last")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "ea56419a-7fb4-11ee-b962-0242ac120002", roles = ["User"])
    fun `should create three entries and find max added and id`() {
        val userId = UUID.fromString("ea56419a-7fb4-11ee-b962-0242ac120002")

        val lastWeek = LocalDate.now().minusWeeks(1)
        val lastMonth = LocalDate.now().minusMonths(1)

        val entry1 = entry(userId)
        entry1.added = lastWeek

        val entry2 = entry(userId)
        entry2.added = lastWeek

        val entry3 = entry(userId)
        entry3.added = lastMonth

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

        val lastEntry = When {
            get("/last")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(WeightTracker::class.java)
        }

        assert(lastEntry.added == entry2.added)
        assert(lastEntry.userId == entry2.userId)
    }

    private fun entry(userId: UUID): WeightTracker {
        val entry = WeightTracker(
            amount = 100f
        )

        entry.userId = userId
        entry.added = LocalDate.now()

        return entry
    }
}