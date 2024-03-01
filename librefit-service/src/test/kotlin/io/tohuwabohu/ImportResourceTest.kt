package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.csv.ImportConfig
import org.junit.jupiter.api.Test
import java.io.File

@QuarkusTest
@TestHTTPEndpoint(ImportResource::class)
class ImportResourceTest {
    @Test
    @TestSecurity(user = "e24c313c-7fb2-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test@libre.fitness"),
        ]
    )
    fun `should upload calorie tracker entry`() {
        val happyFlowCsv = File(this.javaClass.classLoader.getResource("test-csv-happy-flow.csv")!!.toURI())
        val importConfig = ImportConfig()

        Given {
            header("Content-Type", "multipart/form-data")
            multiPart("fileName", "test-csv-happy-flow.csv", "text/plain")
            multiPart("config", importConfig, "application/json")
            multiPart("file", happyFlowCsv, "application/octet-stream")
        } When {
            post("/bulk")
        } Then {
            statusCode(200)
        }
    }
}