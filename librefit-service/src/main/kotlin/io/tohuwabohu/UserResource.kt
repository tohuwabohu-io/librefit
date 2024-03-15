package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.*
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import io.tohuwabohu.security.generateAccessToken
import io.tohuwabohu.security.generateRefreshToken
import io.tohuwabohu.security.printAuthenticationInfo
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDateTime
import java.util.*

@Path("/user")
@RequestScoped
class UserResource(val userRepository: LibreUserRepository,
                   val authRepository: AuthRepository,
                   val activationRepository: AccountActivationRepository) {
    @Inject
    lateinit var jwt: JsonWebToken

    @ConfigProperty(name = "libreuser.tokens.access.expiration.minutes", defaultValue = "25")
    private lateinit var ttlMinutesAccess: String

    @ConfigProperty(name = "libreuser.tokens.refresh.expiration.minutes", defaultValue = "1440")
    private lateinit var ttlMinutesRefresh: String

    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun register(libreUser: LibreUser): Uni<Response> {
        libreUser.registered = LocalDateTime.now()

        Log.info("Registering a new user=${libreUser.email}")

        return userRepository.createUser(libreUser).chain { user -> activationRepository.createAccountActivation(user!!.id!!) }
            .onItem().transform { Response.ok(libreUser).status(Response.Status.CREATED).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = AuthInfo::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
        )]),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun login(libreUser: LibreUser): Uni<Response> {
        return userRepository.findByEmailAndPassword(libreUser.email, libreUser.password).flatMap { user ->
            authRepository.addSession(
                userId = user!!.id!!,
                access = generateAccessToken(user, ttlMinutesAccess.toInt()),
                refresh = generateRefreshToken(ttlMinutesRefresh.toInt())
            )
        }.onItem().transform { authenticationResponse ->
            Response.ok(authenticationResponse).build()
        }.onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND).build() }
        .onFailure().invoke{ e -> Log.error(e) }
        .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun logout(@Context securityContext: SecurityContext, authInfo: AuthInfo): Uni<Response> {
        Log.info("Logout user ${jwt.name}")

        printAuthenticationInfo(jwt, securityContext)

        return authRepository.invalidateSession(authInfo.refreshToken)
            .onItem().transform { _ -> Response.ok().build() }
            .onFailure().invoke{ e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @POST
    @Path("/refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = LibreUser::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "403", description = "Forbidden"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun refreshToken(authInfo: AuthInfo): Uni<Response> {
        return authRepository.findSession(authInfo.refreshToken)
            .flatMap { authSession -> userRepository.findById(authSession!!.userId) }.chain { user ->
                authRepository.invalidateSession(authInfo.refreshToken)
                    .flatMap { authRepository.addSession(
                        userId = user.id!!,
                        access = generateAccessToken(user, ttlMinutesAccess.toInt()),
                        refresh = generateRefreshToken(ttlMinutesRefresh.toInt())
                    ) }
        }.onItem().transform { authenticationResponse -> Response.ok(authenticationResponse).build() }
            .onFailure().invoke{ e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = LibreUser::class)
            )
        ]),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "readUserInfo"
    )
    fun readUserInfo(): Uni<Response> {
        return userRepository.findById(UUID.fromString(jwt.name))
            .onItem().ifNotNull().transform { user ->
                user.password = ""
                Response.ok(user).build()
            }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND).build() }
            .onFailure().invoke{ e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = LibreUser::class)
            )
        ]),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "updateUserInfo"
    )
    fun updateUserInfo(@Context securityContext: SecurityContext, @Valid libreUser: LibreUser): Uni<Response> {
        Log.info("Update user profile $libreUser")

        printAuthenticationInfo(jwt, securityContext)

        return userRepository.updateUser(libreUser, jwt)
            .onItem().ifNotNull().transform { updated ->
                updated!!.password = ""
                Response.ok(updated).build()
            }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/activate/{activationId}")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "400", description = "Bad Request"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun activate(activationId: String): Uni<Response> {
        Log.info("Activating user profile $activationId")

        return activationRepository.findByActivationId(activationId).onItem().ifNotNull().call { activation ->
            userRepository.activateUser(activation!!.userId!!).chain { _ -> activationRepository.deleteEntry(activation.getPrimaryKey()) }
        }.onItem().transform { _ -> Response.ok().build() }
        .onFailure().invoke { e -> Log.error(e) }
        .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }
}