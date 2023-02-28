package io.tohuwabohu

import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.LibreUserRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
class UserResource {

    @Inject
    lateinit var userRepository: LibreUserRepository

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    fun register(
        @FormParam("name") name: String,
        @FormParam("email") email: String,
        @FormParam("password") password: String
    ): Response {
        var response = Response.ok()

        try {
            val libreUser = LibreUser()
            libreUser.name = name
            libreUser.email = email
            libreUser.password = password
            libreUser.registered = LocalDateTime.now()

            userRepository.create(libreUser)
        } catch (ex: Exception) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)

            ex.printStackTrace()
        }

        return response.build()
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    fun login(@FormParam("email") email: String, @FormParam("password") password: String): Response {
        var response = Response.ok()

        val libreUser = userRepository.findByEmailAndPassword(email, password)

        if (libreUser == null) {
            response = Response.status(Response.Status.NOT_FOUND)
        }

        return response.build()
    }
}