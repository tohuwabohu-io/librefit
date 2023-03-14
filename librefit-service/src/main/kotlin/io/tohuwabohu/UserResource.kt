package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.LibreUserRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDateTime
import javax.enterprise.context.RequestScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
@RequestScoped
class UserResource(val userRepository: LibreUserRepository) {

    @POST
    @Path("/register")
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

        Log.info("Registering a new user=$libreUser")

        return userRepository.createUser(libreUser)
            .onItem().transform { Response.ok(libreUser).status(Response.Status.CREATED).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @POST
    @Path("/login")
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
    fun login(libreUser: LibreUser): Uni<Response> {
        return userRepository.findByEmailAndPassword(libreUser.email, libreUser.password)
            .onItem().ifNotNull().transform { user -> Response.ok(user).build() }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND).build() }
            .onFailure().invoke{ e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }
}