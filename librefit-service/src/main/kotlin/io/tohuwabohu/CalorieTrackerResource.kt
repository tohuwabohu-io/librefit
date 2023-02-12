package io.tohuwabohu

import io.tohuwabohu.crud.CalorieTrackerRepository
import io.tohuwabohu.crud.CalorieTrackerEntry
import java.time.LocalDateTime
import javax.inject.Inject
import javax.persistence.EntityExistsException
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/tracker/calories")
class CalorieTrackerResource {

    @Inject
    lateinit var calorieTrackerRepository: CalorieTrackerRepository

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(calorieTracker: CalorieTrackerEntry): Response {
        calorieTracker.added = LocalDateTime.now()

        var response = Response.ok()

        try {
            calorieTrackerRepository.create(calorieTracker)
        } catch (ex: EntityExistsException) {
            response = Response.status(Response.Status.BAD_REQUEST)

            ex.printStackTrace()
        }

        return response.build()
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    fun update(calorieTracker: CalorieTrackerEntry): Response {
        var response = Response.ok()

        val trackingEntry = calorieTrackerRepository.findById(calorieTracker.id!!)

        if (trackingEntry != null) {
            trackingEntry.amount = calorieTracker.amount
            trackingEntry.description = calorieTracker.description

            if (calorieTrackerRepository.updateTrackingEntry(trackingEntry) < 1) {
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            }
        } else {
            response = Response.status(Response.Status.NOT_FOUND)
        }

        return response.build()
    }

    @GET
    @Path("/read/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(id: String) = calorieTrackerRepository.findById(id.toLong())

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    fun delete(calorieTracker: CalorieTrackerEntry): Response {
        return if (calorieTrackerRepository.deleteById(calorieTracker.id!!)) {
            Response.ok().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @GET
    @Path("/list/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: String): List<CalorieTrackerEntry> {
        return calorieTrackerRepository.listForUser(userId.toLong())
    }
}
