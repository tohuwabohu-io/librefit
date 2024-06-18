package io.tohuwabohu

import io.quarkus.test.TestReactiveTransaction
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.vertx.RunOnVertxContext
import io.quarkus.test.vertx.UniAsserter
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.crud.CalorieTrackerRepository
import io.tohuwabohu.crud.ImportConfig
import io.tohuwabohu.crud.WeightTrackerRepository
import jakarta.inject.Inject
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

@QuarkusTest
@TestHTTPEndpoint(ImportResource::class)
class ImportResourceTest {
    @Inject
    lateinit var calorieTrackerRepository: CalorieTrackerRepository

    @Inject
    lateinit var weightTrackerRepository: WeightTrackerRepository

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120001", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and append tracker entries`(uniAsserter: UniAsserter) {
        val happyFlowCsv = File(this.javaClass.classLoader.getResource("import-test-happy-flow-1.csv")!!.toURI())
        val importConfig = ImportConfig()

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-happy-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(200)
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120001")) },
            { result -> Assertions.assertEquals(20, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120001")) },
            { result -> Assertions.assertEquals(5, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ef-b962-0242ac120002", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and drop duplicate tracker entries`(uniAsserter: UniAsserter) {
        val happyFlowCsv1 = File(this.javaClass.classLoader.getResource("import-test-happy-flow-1.csv")!!.toURI())
        val happyFlowCsv2 = File(this.javaClass.classLoader.getResource("import-test-happy-flow-2.csv")!!.toURI())

        val importConfig = ImportConfig()

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-happy-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv1, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(200)
        }

        importConfig.drop = true

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-happy-flow-2.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv2, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(200)
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ef-b962-0242ac120002")) },
            { result -> Assertions.assertEquals(30, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ef-b962-0242ac120002")) },
            { result -> Assertions.assertEquals(6, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120003", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and fail input parsing`(uniAsserter: UniAsserter) {
        val errorFlowCsv1 = File(this.javaClass.classLoader.getResource("import-test-parse-error-flow-1.csv")!!.toURI())

        val importConfig = ImportConfig()

        // wrong date format in data
        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-parse-error-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", errorFlowCsv1, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("datePattern"))
        }

        importConfig.datePattern = "dd-MM-yyyy"

        val errorFlowCsv2 = File(this.javaClass.classLoader.getResource("import-test-parse-error-flow-2.csv")!!.toURI())

        // calorie tracker amount field contains NaN
        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-parse-error-flow-2.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", errorFlowCsv2, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("amount"))
        }

        // weight tracker amount field contains NaN
        val errorFlowCsv3 = File(this.javaClass.classLoader.getResource("import-test-parse-error-flow-3.csv")!!.toURI())

        // calorie tracker amount field contains NaN
        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-parse-error-flow-2.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", errorFlowCsv3, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("amount"))
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120003")) },
            { result -> Assertions.assertEquals(0, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120003")) },
            { result -> Assertions.assertEquals(0, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120004", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and fail input validation`(uniAsserter: UniAsserter) {
        val importConfig = ImportConfig()
        val errorFlowCsv1 = File(this.javaClass.classLoader.getResource("import-test-validation-error-flow-1.csv")!!.toURI())

        // -1 value for calorie tracker amount
        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-validation-error-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", errorFlowCsv1, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("amount"))
        }

        val errorFlowCsv2 = File(this.javaClass.classLoader.getResource("import-test-validation-error-flow-2.csv")!!.toURI())

        // -1 value for weight tracker amount
        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-validation-error-flow-2.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", errorFlowCsv2, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("amount"))
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120004")) },
            { result -> Assertions.assertEquals(0, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120004")) },
            { result -> Assertions.assertEquals(0, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120005", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and fail due to wrong header count`(uniAsserter: UniAsserter) {
        val errorFlowCsv4 = File(this.javaClass.classLoader.getResource("import-test-validation-error-flow-3.csv")!!.toURI())

        val importConfig = ImportConfig()
        importConfig.headerLength = 1

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-validation-error-flow-3.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", errorFlowCsv4, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120005")) },
            { result -> Assertions.assertEquals(0, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120005")) },
            { result -> Assertions.assertEquals(0, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120006", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and update calorie tracker exclusively`(uniAsserter: UniAsserter) {
        val happyFlowCsv = File(this.javaClass.classLoader.getResource("import-test-happy-flow-1.csv")!!.toURI())
        val importConfig = ImportConfig()
        importConfig.updateCalorieTracker = true
        importConfig.updateWeightTracker = false

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-happy-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(200)
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120006")) },
            { result -> Assertions.assertEquals(20, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120006")) },
            { result -> Assertions.assertEquals(0, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120007", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and update weight tracker exclusively`(uniAsserter: UniAsserter) {
        val happyFlowCsv = File(this.javaClass.classLoader.getResource("import-test-happy-flow-1.csv")!!.toURI())
        val importConfig = ImportConfig()
        importConfig.updateCalorieTracker = false
        importConfig.updateWeightTracker = true

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-happy-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(200)
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120007")) },
            { result -> Assertions.assertEquals(0, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120007")) },
            { result -> Assertions.assertEquals(5, result.size)}
        )
    }

    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120008", roles = ["User"])
    @TestReactiveTransaction
    @RunOnVertxContext
    fun `should upload and fail due to unselected importer`(uniAsserter: UniAsserter) {
        val happyFlowCsv = File(this.javaClass.classLoader.getResource("import-test-happy-flow-1.csv")!!.toURI())
        val importConfig = ImportConfig()
        importConfig.updateCalorieTracker = false
        importConfig.updateWeightTracker = false

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "import-test-happy-flow-1.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(400)
            body("errors[0].field", Matchers.equalTo("importer"))
        }

        uniAsserter.assertThat(
            { calorieTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120008")) },
            { result -> Assertions.assertEquals(0, result.size) }
        )

        uniAsserter.assertThat (
            { weightTrackerRepository.listEntriesForUser(UUID.fromString("e24c313c-7fb2-11ee-b962-0242ac120008")) },
            { result -> Assertions.assertEquals(0, result.size)}
        )
    }
}