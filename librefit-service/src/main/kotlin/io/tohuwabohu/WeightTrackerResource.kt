package io.tohuwabohu

import io.tohuwabohu.crud.WeightTrackerEntry
import io.tohuwabohu.crud.WeightTrackerRepository
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/tracker/weight")
class WeightTrackerResource(val weightTrackerRepository: WeightTrackerRepository) {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(weightTrackerEntry: WeightTrackerEntry) {
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