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
import io.tohuwabohu.crud.AuthInfo
import io.tohuwabohu.crud.LibreUser
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
class UserResourceTest {
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
            header("Content-Type", "application/json")
            body(user("duplicate-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun `should login user`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("login-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        Given {
            header("Content-Type", "application/json")
            body(user("login-test@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `should fail on login`() {
        Given {
            header("Content-Type", "application/json")
            body(user("not-existing@test.dev"))
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
    fun `should return user data`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("data-test@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

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
    fun `should update user data`() {
        val userOriginal = user("update-test@test.dev")
        val user = LibreUser(
            email = "unit-test1@test.dev",
            password = "password",
            avatar = "/path"
        )

        Given {
            header("Content-Type", ContentType.JSON)
            body(userOriginal)
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

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
    @JwtSecurity(
        claims = [
            Claim(key = "email", value = "test1@test.dev"),
        ]
    )
    fun `should fail on updating user data with wrong password`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("wrong-pwd@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

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
    @TestSecurity(user = "11e45d14-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should login and logout a user`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("auth-test1@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        val authInfo = Given {
            header("Content-Type", "application/json")
            body(user("auth-test1@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        Given {
            header("Content-Type", "application/json")
            body(AuthInfo(accessToken = "", refreshToken = authInfo.refreshToken))
        } When {
            post("/logout")
        } Then {
            statusCode(200)
        }
    }

    @Test
    @TestSecurity(user = "11e45d14-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should login a user 4 times and invalidate the oldest refresh token`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("auth-test2@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        val authInfo1 = Given {
            header("Content-Type", "application/json")
            body(user("auth-test2@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        Given {
            header("Content-Type", "application/json")
            body(user("auth-test2@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        }

        Given {
            header("Content-Type", "application/json")
            body(user("auth-test2@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        Given {
            header("Content-Type", "application/json")
            body(user("auth-test2@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        Given {
            header("Content-Type", "application/json")
            body(AuthInfo(accessToken = "", refreshToken = authInfo1.refreshToken))
        } When {
            post("/refresh")
        } Then {
            statusCode(403)
        }
    }

    @Test
    @TestSecurity(user = "11e45d14-7fb5-11ee-b962-0242ac120002", roles = ["User"])
    fun `should login a user and rotate the refresh token`() {
        Given {
            header("Content-Type", ContentType.JSON)
            body(user("auth-test3@test.dev"))
        } When {
            post("/register")
        } Then {
            statusCode(201)
        }

        val authInfo1 = Given {
            header("Content-Type", "application/json")
            body(user("auth-test3@test.dev"))
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        val authInfo2 = Given {
            header("Content-Type", "application/json")
            body(authInfo1)
        } When {
            post("/refresh")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        assert(authInfo1.accessToken != authInfo2.accessToken)
        assert(authInfo1.refreshToken != authInfo2.refreshToken)

        Given {
            header("Content-Type", "application/json")
            body(authInfo1)
        } When {
            post("/refresh")
        } Then {
            statusCode(403)
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

        val authInfo = Given {
            header("Content-Type", ContentType.JSON)
            body(user)
        } When {
            post("/login")
        } Then {
            statusCode(200)
        } Extract {
            body().`as`(AuthInfo::class.java)
        }

        Given {
            header("Authorization", "Bearer ${authInfo.accessToken}")
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