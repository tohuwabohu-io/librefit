package io.tohuwabohu

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.tohuwabohu.crud.LibreUser
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation::class)
@QuarkusTest
class UserResourceTest {


    @Test
    @Order(1)
    fun `should register user`() {
        given()
            .header("Content-Type", ContentType.JSON)
            .body(user())
            .post("/user/register")
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
            .post("/user/register")
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
            .post("/user/login")
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
            .post("/user/login")
            .then()
            .assertThat()
            .statusCode(404)
    }

    private fun user(): LibreUser {
        val user = LibreUser()
        user.email = "test1@test.dev"
        user.password = "tastb1"
        user.name = "testname"

        return user
    }

    private fun failingUser(): LibreUser {
        val failingUser = LibreUser()
        failingUser.email = "test1@test.dev"
        failingUser.password = "otherpasswordthanuser2b"
        failingUser.name = "nottestname"

        return failingUser
    }
}