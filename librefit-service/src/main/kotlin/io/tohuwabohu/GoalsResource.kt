package io.tohuwabohu

import io.tohuwabohu.crud.Goal
import io.tohuwabohu.crud.GoalsRepository
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/goals")
class GoalsResource {

    @Inject
    lateinit var goalsRepository: GoalsRepository

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    fun create(goal: Goal) = goalsRepository.create(goal)

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    fun update(goal: Goal) {
        val goalEntry = goalsRepository.findById(goal.id!!)

        if (goalEntry != null) {
            goalEntry.startAmount = goal.startAmount
            goalEntry.endAmount = goal.endAmount
            goalEntry.startDate = goal.startDate
            goalEntry.endDate = goal.endDate

            goalsRepository.updateGoal(goalEntry)
        }
    }

    @GET
    @Path("/read/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(id: Long) = goalsRepository.findById(id)

    @DELETE
    @Path("/delete/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun delete(id: Long) = goalsRepository.deleteById(id)

    @GET
    @Path("/list/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: Long): List<Goal> {
        return goalsRepository.listByUser(userId)
    }

    @GET
    @Path("/latest/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun latest(userId: Long): Goal {
        return goalsRepository.findLatestForUser(userId)
    }
}