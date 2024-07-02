package io.tohuwabohu

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.tohuwabohu.crud.LibreUser
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
class UserResourceTest {
    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    lateinit var authCookieName: String

    @Test
    fun `should register user`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("register-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }
    }

    @Test
    fun `should fail on duplicate email registration`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("duplicate-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        Given {
            header("Content-Type", ContentType.JSON)
            body(user("duplicate-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun `should register, login, logout a user`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("login-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        val authToken = Given {
            header("Content-Type", ContentType.MULTIPART)
            multiPart("email", "login-test@test.dev", "text/plain")
            multiPart("password", "tastb1", "text/plain")
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            detailedCookie(authCookieName)
        }

        Given {
            cookie(authToken)
            header("Content-Type", ContentType.JSON)
        } When {
            post("/logout")
        } Then {
            statusCode(204)
        }
    }

    @Test
    fun `should fail on login`() {
        Given {
            header("Content-Type", ContentType.MULTIPART)
            multiPart("email", "not-existing@test.dev", "text/plain")
            multiPart("password", "test1234", "text/plain")
        } When {
            post("/login")
        } Then {
            statusCode(401)
        }
    }

    @Test
    @TestSecurity(user = "1171b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
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
    fun `should fail on reading user data`() {
        When {
            get("/read")
        } Then {
            statusCode(404)
        }
    }

    @Test
    @TestSecurity(user = "1171b08c-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should update user data`() {
        val user = LibreUser(
            email = "unit-test1@test.dev",
            password = "password",
            name = "Fridolin",
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
    fun `should fail on updating user data with wrong password`() {
        val user = user("wrong-pwd@test.dev")
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

    @Test
    fun `should register user, login and return profile`() {
        val user = user("profile-test1@test.dev")
        user.avatar = "/path"
        user.name = "Testname"

        Given {
            header("Content-Type", ContentType.JSON)
            body(user)
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        val authToken = Given {
            header("Content-Type", ContentType.MULTIPART)
            multiPart("email", user.email, "text/plain")
            multiPart("password", user.password, "text/plain")
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            detailedCookie(authCookieName)
        }

        Given {
            cookie(authToken)
        } When {
            get("/read")
        } Then {
            statusCode(200)
            body("email", equalTo(user.email))
            body("avatar", equalTo(user.avatar))
            body("name", equalTo(user.name))
            body("password", equalTo(""))
        }
    }

    @Test
    fun `should fail due to not meeting password requirements`() {
        val user = user("profile-test2@test.dev")
        user.password = "12345"

        Given {
            header("Content-Type", ContentType.JSON)
            body(user)
        } When {
            post("/register")
        } Then {
            statusCode(400)
        }
    }

    private fun user(email: String): LibreUser {
        return LibreUser(
            email = email,
            password = "tastb1",
            name = "testname",
        )
    }

    private fun invalidUser(): LibreUser {
        return LibreUser(
            email = "invalid@test.dev",
            password = ""
        )
    }
}