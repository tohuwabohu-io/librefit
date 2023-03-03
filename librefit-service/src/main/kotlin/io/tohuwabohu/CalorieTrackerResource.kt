package io.tohuwabohu

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.CalorieTrackerRepository
import io.tohuwabohu.crud.CalorieTrackerEntry
import java.time.LocalDateTime
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/tracker/calories")
@RequestScoped
class CalorieTrackerResource(val calorieTrackerRepository: CalorieTrackerRepository) {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    fun create(calorieTracker: CalorieTrackerEntry): Uni<Response> {
        calorieTracker.added = LocalDateTime.now()

        return calorieTrackerRepository.persistAndFlush(calorieTracker).onItem()
            .transform { entry -> Response.ok(entry).status(Response.Status.CREATED).build() }.onFailure()
            .recoverWithItem(Response.serverError().build())
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    fun update(calorieTracker: CalorieTrackerEntry): Uni<Response> =
        calorieTrackerRepository.findById(calorieTracker.id!!).onItem().ifNull().failWith { EntityNotFoundException() }
            .onItem().ifNotNull().invoke(calorieTrackerRepository::updateTrackingEntry)
            .map { entry -> Response.ok(entry).build() }.onFailure(EntityNotFoundException::class.java)
            .recoverWithItem(Response.status(Response.Status.NOT_FOUND).build()).onFailure()
            .recoverWithItem(Response.serverError().build())

    @GET
    @Path("/read/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(id: Long): Uni<Response> =
        calorieTrackerRepository.findById(id).onItem().ifNull().failWith(EntityNotFoundException()).onItem().ifNotNull()
            .transform { entry -> Response.ok(entry).build() }.onFailure(EntityNotFoundException::class.java)
            .recoverWithItem(Response.status(Response.Status.NOT_FOUND).build()).onFailure()
            .recoverWithItem(Response.serverError().build())

    @DELETE
    @Path("/delete/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    fun delete(id: Long): Uni<Response> =
        calorieTrackerRepository.deleteById(id).onItem().ifNotNull().transform { deleted ->
            if (deleted == true) Response.ok().build() else Response.notModified().build()
        }.onFailure().recoverWithItem(Response.serverError().build())

    @GET
    @Path("/list/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: Long): Uni<Response> =
        calorieTrackerRepository.listForUser(userId).onItem().transform { Response.ok(it).build() }.onFailure()
            .recoverWithItem(Response.serverError().build())
}
