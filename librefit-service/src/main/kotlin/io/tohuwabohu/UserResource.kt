package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.LibreUserRepository
import io.tohuwabohu.crud.LibreUserValidation
import io.tohuwabohu.crud.validation.ValidationError
import java.time.LocalDateTime
import javax.enterprise.context.RequestScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
@RequestScoped
class UserResource(val userRepository: LibreUserRepository, val validation: LibreUserValidation) {

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    fun register(
        @FormParam("name") name: String,
        @FormParam("email") email: String,
        @FormParam("password") password: String
    ): Uni<Response> {
        val libreUser = LibreUser()
        libreUser.name = name
        libreUser.email = email
        libreUser.password = password
        libreUser.registered = LocalDateTime.now()

        Log.info("Registering a new user=$libreUser")

        return userRepository.createUser(libreUser)
            .onItem().transform { Response.ok(libreUser).status(Response.Status.CREATED).build() }
            .invoke { e -> Log.error(e) }
            .onFailure().invoke(Log::error)
            .onFailure().recoverWithItem { throwable ->
                if (throwable is ValidationError) {
                    Response.status(Response.Status.BAD_REQUEST).entity(throwable).build()
                } else {
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
                }
            }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    fun login(@FormParam("email") email: String, @FormParam("password") password: String): Uni<Response> {
        Log.info("Searching user with email=$email and pwd=$password")

        return userRepository.findByEmailAndPassword(email, password)
            .onItem().ifNotNull().transform { user -> Response.ok(user).build() }
            .onItem().ifNull().continueWith { Response.status(Response.Status.NOT_FOUND).build() }
            .onFailure().invoke(Log::error)
            .onFailure().recoverWithItem(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())
    }
}