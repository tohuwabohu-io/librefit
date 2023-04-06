package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.WeightTrackerEntry
import io.tohuwabohu.crud.WeightTrackerRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/tracker/weight")
class WeightTrackerResource(val weightTrackerRepository: WeightTrackerRepository) {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "201", description = "Created", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WeightTrackerEntry::class)
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
        operationId = "createWeightTrackerEntry"
    )
    fun create(@Valid weightTrackerEntry: WeightTrackerEntry): Uni<Response> {
        Log.info("Creating a new weight tracker entry=$weightTrackerEntry")

        return weightTrackerRepository.validateAndPersist(weightTrackerEntry)
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
        operationId = "updateWeightTrackerEntry"
    )
    fun update(@Valid weightTracker: WeightTrackerEntry): Uni<Response> {
        Log.info("Updating weight tracker entry $weightTracker")

        return weightTrackerRepository.updateTrackingEntry(weightTracker)
            .onItem().transform { rowCount -> if (rowCount > 0) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke{ e -> Log.error(e)}
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @DELETE
    @Path("/delete/{userId:\\d+}/{date}/{id:\\d+}")
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
        operationId = "deleteWeightTrackerEntry"
    )
    fun delete(userId: Long, date: LocalDate, id: Long): Uni<Response> {
         Log.info("Delete weight tracker entry with userId=$userId added=$date id=$id")

        return weightTrackerRepository.deleteTrackingEntry(userId, date, id)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem {throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/read/{userId:\\d+}/{date}/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WeightTrackerEntry::class)
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
        operationId = "readWeightTrackerEntry"
    )
    fun read(userId: Long, date: LocalDate, id: Long): Uni<Response> =
        weightTrackerRepository.readEntry(userId, date, id)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }

    @GET
    @Path("/list/{userId:\\d+}/{date}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<WeightTrackerEntry>::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "listWeightTrackerEntries"
    )
    fun listEntries(userId: Long, date: LocalDate): Uni<Response> =
        weightTrackerRepository.listEntriesForUserAndDate(userId, date)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }

    @GET
    @Path("/list/{userId:\\d+}/{dateFrom}/{dateTo}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<WeightTrackerEntry>::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "listWeightTrackerEntriesRange"
    )
    fun listEntries(userId: Long, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> =
        weightTrackerRepository.listEntriesForUserAndDateRange(userId, dateFrom, dateTo)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }

    @GET
    @Path("/last/{userId:\\d+}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WeightTrackerEntry::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "findLastWeightTrackerEntry"
    )
    fun findLast(userId: Long): Uni<Response> = weightTrackerRepository.findLastEntry(userId)
        .onItem().transform { entry -> Response.ok(entry).build() }
        .onFailure().invoke { e -> Log.error(e) }
        .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
}