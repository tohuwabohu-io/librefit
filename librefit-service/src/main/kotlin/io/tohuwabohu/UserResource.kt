package io.tohuwabohu

import io.tohuwabohu.crud.LibreUser
import io.tohuwabohu.crud.LibreUserRepository
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/user")
class UserResource {

    @Inject
    lateinit var userRepository: LibreUserRepository

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "User"

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    fun register(libreUser: LibreUser) = userRepository.create(libreUser)
}