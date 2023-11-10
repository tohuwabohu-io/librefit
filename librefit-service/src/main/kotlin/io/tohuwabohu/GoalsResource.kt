package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.Goal
import io.tohuwabohu.crud.GoalsRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import io.tohuwabohu.security.printAuthenticationInfo
import io.tohuwabohu.security.validateToken
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import java.util.*

@Path("/goals")
class GoalsResource(val goalsRepository: GoalsRepository) {
    @Inject
    lateinit var jwt: JsonWebToken

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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "createGoal"
    )
    fun create(@Context securityContext: SecurityContext, @Valid goal: Goal): Uni<Response> {
        Log.info("Creating a new goal=$goal")

        printAuthenticationInfo(jwt, securityContext)

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
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "updateGoal"
    )
    fun update(@Context securityContext: SecurityContext, @Valid goal: Goal): Uni<Response> {
        Log.info("updating goal $goal")

        printAuthenticationInfo(jwt, securityContext)
        validateToken(jwt, goal)

        return goalsRepository.updateEntry(goal, Goal::class.java)
            .onItem().transform { updated -> Response.ok(updated).build() }
            .onFailure().invoke{ e -> Log.error(e)}
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/read/{date}/{id:\\d+}")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "readGoal"
    )
    fun read(@Context securityContext: SecurityContext, date: LocalDate, id: Long): Uni<Response> =
        goalsRepository.readEntry(UUID.fromString(jwt.name), date, id)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }

    @DELETE
    @Path("/delete/{date}/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "deleteGoal"
    )
    fun delete(@Context securityContext: SecurityContext, date: LocalDate, id: Long): Uni<Response> {
        Log.info("deleting goal with added=$date id=$id")

        printAuthenticationInfo(jwt, securityContext)

        return goalsRepository.deleteEntry(UUID.fromString(jwt.name), date, id)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem {throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/last")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Goal::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "findLastGoal"
    )
    fun latest(@Context securityContext: SecurityContext): Uni<Response> = goalsRepository.findLastGoal(UUID.fromString(jwt.name))
        .onItem().transform { entry -> Response.ok(entry).build() }
        .onFailure().invoke { e -> Log.error(e) }
        .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
}