package io.tohuwabohu

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.tohuwabohu.crud.LibreUser
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation::class)
@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
class UserResourceTest {
    @Test
    @Order(1)
    fun `should register user`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(user())
            .post("/register")
            .then()
            .assertThat()
            .statusCode(201)
    }

    @Test
    @Order(2)
    fun `should fail on duplicate email registration`() {
        given()
            .header("Content-Type", "application/json")
            .body(failingUser())
            .post("/register")
            .then()
            .assertThat()
            .statusCode(400)
    }

    @Test
    @Order(3)
    fun `should login user`() {
        given()
            .header("Content-Type", "application/json")
            .body(user())
            .post("/login")
            .then()
            .assertThat()
            .statusCode(200)
    }

    @Test
    @Order(4)
    fun `should fail on login`() {
        given()
            .header("Content-Type", "application/json")
            .body(failingUser())
            .post("/login")
            .then()
            .assertThat()
            .statusCode(404)
    }

    @Test
    @TestSecurity(user = "1", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test1@test.dev"),
        ]
    )
    @Order(5)
    fun `should return user data`() {
        val userData = given().get("/user").then()

        userData.assertThat().statusCode(200)

        val user = userData.extract().body().`as`(LibreUser::class.java)

        assert(user.email == "test1@test.dev")
        assert(user.password == "")
    }

    @Test
    @TestSecurity(user = "2", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test2@test.dev")
        ]
    )
    @Order(6)
    fun `should fail on reading user data`() {
        given().get("/user").then().assertThat().statusCode(404)
    }

    private fun user(): LibreUser {
        return LibreUser(
            email = "test1@test.dev",
            password = "tastb1",
            name = "testname",
        )
    }

    private fun failingUser(): LibreUser {
        return LibreUser(
            email = "test1@test.dev",
            password = "otherpasswordthanuser2b",
            name = "nottestname"
        )
    }
}