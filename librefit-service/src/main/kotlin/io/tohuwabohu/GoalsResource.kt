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
    @Path("/read/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(id: String) = goalsRepository.findById(id.toLong())

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    fun delete(goal: Goal) = goalsRepository.delete(goal)

    @GET
    @Path("/list/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: String): List<Goal> {
        return goalsRepository.listByUser(userId.toLong())
    }

    @GET
    @Path("/latest/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun latest(userId: String): Goal {
        return goalsRepository.findLatestForUser(userId.toLong())
    }
}