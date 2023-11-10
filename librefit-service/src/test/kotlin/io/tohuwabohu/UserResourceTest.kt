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
import io.tohuwabohu.crud.LibreUser
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.util.*

@TestMethodOrder(OrderAnnotation::class)
@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
class UserResourceTest {
    @Test
    @Order(1)
    fun `should register user`() {
        val user = user()
        user.id = UUID.fromString("1171b08c-7fb5-11ee-b962-0242ac120002")

        Given {
            header("Content-Type", ContentType.JSON)
            body(user())
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

    }

    @Test
    @Order(2)
    fun `should fail on duplicate email registration`() {
        Given {
            header("Content-Type", "application/json")
            body(failingUser())
        } When {
            post("/register")
        } Then {
            statusCode(400)
        }
    }

    @Test
    @Order(3)
    fun `should login user`() {
        Given {
            header("Content-Type", "application/json")
            body(user())
        } When {
            post("/login")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @Order(4)
    fun `should fail on login`() {
        Given {
            header("Content-Type", "application/json")
            body(failingUser())
        } When {
            post("/login")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "1171b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "unit-test1@test.dev")
        ]
    )
    @Order(5)
    fun `should return user data`() {
        When {
            get("/read")
        } Then {
            statusCode(200)
            body("email", equalTo("unit-test1@test.dev"))
            body("password", equalTo(""))
        } Extract {
            body().`as`(LibreUser::class.java)
        }
    }

    @Test
    @TestSecurity(user = "2271b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "unit-test2@test.dev")
        ]
    )
    @Order(6)
    fun `should fail on reading user data`() {
        When {
            get("/read")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "1171b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "unit-test1@test.dev"),
        ]
    )
    @Order(7)
    fun `should update user data`() {
        val user = LibreUser(
            email = "unit-test1@test.dev",
            password = "test1",
            avatar = "/path"
        )

        Given {
            header("Content-Type", "application/json")
            body(user)
        } When {
            post("/update")
        } Then {
            statusCode(200)
            body("email", equalTo(user.email))
            body("avatar", equalTo(user.avatar))
            body("password", equalTo(""))
        }

        When {
            get("/read")
        } Then {
            statusCode(200)
            body("email", equalTo(user.email))
            body("avatar", equalTo(user.avatar))
            body("password", equalTo(""))
        }
    }

    @Test
    @TestSecurity(user = "330ff8f4-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test3@test.dev"),
        ]
    )
    @Order(8)
    fun `should fail on updating user data` () {
        val user = user()
        user.avatar = "/path"

        Given {
            header("Content-Type", "application/json")
            body(user)
        } When {
            post("/update")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(9)
    fun `should fail registration validation`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(invalidUser())
        } When {
            post("/register")
        } Then {
            statusCode(400)
        }
    }

    @Test
    @TestSecurity(user = "11e45d14-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test1@test.dev"),
        ]
    )
    @Order(10)
    fun `should fail on updating user data with wrong password`() {
        val user = user()
        user.avatar = "/path"
        user.password = "notquiteright"

        Given {
            header("Content-Type", "application/json")
            body(user)
        } When {
            post("/update")
        } Then {
            statusCode(404)
        }
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

    private fun invalidUser(): LibreUser {
        return LibreUser(
            email = "invalid@test.dev",
            password = ""
        )
    }
}