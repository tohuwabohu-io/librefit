package io.tohuwabohu

import io.tohuwabohu.crud.WeightTrackerEntry
import io.tohuwabohu.crud.WeightTrackerRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/tracker/weight")
class WeightTrackerResource {
    @Inject
    lateinit var weightTrackerRepository: WeightTrackerRepository

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(weightTrackerEntry: WeightTrackerEntry) {
        weightTrackerEntry.added = LocalDateTime.now()

        weightTrackerRepository.create(weightTrackerEntry)
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    fun update(weightTracker: WeightTrackerEntry) {
        val weightTrackerEntry = weightTrackerRepository.findById(weightTracker.id!!)

        if (weightTrackerEntry != null) {
            weightTrackerEntry.amount = weightTracker.amount

            weightTrackerRepository.updateTrackingEntry(weightTrackerEntry)
        }
    }

    @GET
    @Path("/read/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(id: String) = weightTrackerRepository.findById(id.toLong())

    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun delete(weightTrackerEntry: WeightTrackerEntry) = weightTrackerRepository.delete(weightTrackerEntry)

    @GET
    @Path("/list/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: String) = weightTrackerRepository.listForUser(userId.toLong())
}