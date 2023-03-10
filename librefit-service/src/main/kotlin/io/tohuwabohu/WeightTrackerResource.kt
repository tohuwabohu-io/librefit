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
class WeightTrackerResource(val weightTrackerRepository: WeightTrackerRepository) {
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
            /*weightTrackerEntry.amount = weightTracker.amount

            weightTrackerRepository.updateTrackingEntry(weightTrackerEntry)*/
        }
    }

    @GET
    @Path("/read/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(id: Long) = weightTrackerRepository.findById(id)

    @DELETE
    @Path("/delete/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun delete(id: Long) = weightTrackerRepository.deleteById(id)

    @GET
    @Path("/list/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: Long) = weightTrackerRepository.listForUser(userId)
}