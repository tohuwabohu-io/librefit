package io.tohuwabohu

import io.quarkus.logging.Log
import io.quarkus.security.UnauthorizedException
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.LibreUserRepository
import io.tohuwabohu.crud.error.ErrorHandler.createErrorResponse
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.recoverWithResponse
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.jboss.resteasy.reactive.PartType
import org.jboss.resteasy.reactive.RestForm
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Path("/api/user")
class UserResource(
    private val userRepository: LibreUserRepository
) {
    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    lateinit var cookieName: String

    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(
            responseCode = "400", description = "Bad Request", content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class),
            )]
        ),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "postUserRegister")
    fun register(libreUser: LibreUser): Uni<Response> {
        libreUser.registered = LocalDateTime.now()

        Log.info("Registering a new user=${libreUser.email}")

        return userRepository.createUser(libreUser)
            .onItem().transform { Response.ok(libreUser).status(Response.Status.CREATED).build() }
            .onFailure().recoverWithResponse()
    }

    @POST
    @Path("/login")
    @RolesAllowed("User", "Admin")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(
            responseCode = "400", description = "Bad Request", content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class),
            )]
        ),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "postUserLogin")
    fun login(
        @Context securityIdentity: SecurityIdentity,
        @RestForm @PartType("text/plain") email: String,
        @RestForm @PartType("text/plain") password: String
    ) {
        /** This is just a placeholder to generate the OpenAPI spec.
         * The Quarkus form based authentication intercepts calls to this operation and handles them.
         * @see io.tohuwabohu.security.TrustedLibreUserIdentityProvider.authenticate */
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "204", description = "No Content"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "postUserLogout")
    fun logout(@Context securityIdentity: SecurityIdentity): Uni<Response> {
        return Uni.createFrom().item(securityIdentity).onItem().transformToUni { identity ->
            if (identity.isAnonymous) {
                Uni.createFrom().item(createErrorResponse(UnauthorizedException()))
            } else {
                Uni.createFrom().item(
                    Response.noContent().cookie(
                        NewCookie.Builder(cookieName).maxAge(0).expiry(Date.from(Instant.EPOCH)).path("/").build()
                    ).build()
                )
            }
        }.onFailure().recoverWithResponse()
    }

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "OK", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LibreUser::class)
                )
            ]
        ),
        APIResponse(
            responseCode = "400", description = "Bad Request", content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class)
            )]
        ),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "readUserInfo"
    )
    fun readUserInfo(@Context securityIdentity: SecurityIdentity): Uni<Response> {
        return userRepository.findById(UUID.fromString(securityIdentity.principal.name))
            .onItem().ifNotNull().transform { user ->
                user.password = ""
                Response.ok(user).build()
            }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND).build() }
            .onFailure().recoverWithResponse()
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "OK", content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LibreUser::class)
                )
            ]
        ),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(
            responseCode = "400", description = "Bad Request", content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class)
            )]
        ),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "updateUserInfo"
    )
    fun updateUserInfo(@Context securityIdentity: SecurityIdentity, @Valid libreUser: LibreUser): Uni<Response> {
        Log.info("Update user profile $libreUser")

        return userRepository.updateUser(libreUser,
            UUID.fromString(securityIdentity.principal.name)
        ).onItem().ifNotNull().transform { updated ->
            updated!!.password = ""
            Response.ok(updated).build()
        }.onFailure().recoverWithResponse()
    }
}