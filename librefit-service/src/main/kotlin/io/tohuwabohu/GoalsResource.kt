package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.Goal
import io.tohuwabohu.crud.GoalsRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/goals")
class GoalsResource(val goalsRepository: GoalsRepository) {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "201", description = "Created", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Goal::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class)
            )
        ]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "createGoal"
    )
    fun create(goal: Goal): Uni<Response> {
        Log.info("Creating a new goal=$goal")

        return goalsRepository.validateAndPersist(goal)
            .onItem().transform { entry -> Response.ok(entry).status(Response.Status.CREATED).entity(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{throwable -> createErrorResponse(throwable) }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "304", description = "Not Modified"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "updateGoal"
    )
    fun update(goal: Goal): Uni<Response> {
        Log.info("updating goal $goal")

        return goalsRepository.updateGoal(goal)
            .onItem().transform { rowCount -> if (rowCount > 0) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke{ e -> Log.error(e)}
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/read/{userId:\\d+}/{date}/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Goal::class)
            )
        ]),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "readGoal"
    )
    fun read(userId: Long, date: LocalDate, id: Long): Uni<Response> =
        goalsRepository.readEntry(userId, date, id)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }

    @DELETE
    @Path("/delete/{userId:\\d+}/{date}/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "304", description = "Not Modified"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "deleteGoal"
    )
    fun delete(userId: Long, date: LocalDate, id: Long): Uni<Response> {
        Log.info("deleting goal with userId=$userId added=$date id=$id")

        return goalsRepository.deleteEntry(userId, date, id)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem {throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/list/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(userId: Long): Uni<List<Goal>> = goalsRepository.listByUser(userId)

    @GET
    @Path("/latest/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun latest(userId: Long): Uni<Goal> = goalsRepository.findLatestForUser(userId).onItem().transform { it.first() }
}