package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.CalorieTrackerRepository
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import javax.enterprise.context.RequestScoped
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/tracker/calories")
@RequestScoped
class CalorieTrackerResource(val calorieTrackerRepository: CalorieTrackerRepository) {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "201", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CalorieTrackerEntry::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun create(@Valid calorieTracker: CalorieTrackerEntry): Uni<Response> {
        Log.info("Creating a new calorie tracker entry=$calorieTracker")

        return calorieTrackerRepository.validateAndPersist(calorieTracker)
            .onItem().transform { entry -> Response.ok(entry).status(Response.Status.CREATED).entity(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
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
    fun update(@Valid calorieTracker: CalorieTrackerEntry): Uni<Response> {
        Log.info("Updating calorie tracker entry $calorieTracker")

        return calorieTrackerRepository.updateTrackingEntry(calorieTracker)
            .onItem().transform { rowCount -> if (rowCount > 0) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CalorieTrackerEntry::class)
            )
        ]),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @GET
    @Path("/read/{userId:\\d+}/{date}/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun read(userId: Long, date: LocalDate, id: Long): Uni<Response> =
        calorieTrackerRepository.readEntry(userId, date, id)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }

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
    @DELETE
    @Path("/delete/{userId:\\d+}/{date}/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(userId: Long, date: LocalDate, id: Long): Uni<Response> {
        Log.info("Delete calorie tracker entry with id $id")

        return calorieTrackerRepository.deleteEntry(userId, date, id)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/list/{userId:\\d+}/dates")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<LocalDate>::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun listDates(userId: Long): Uni<Response> =
        calorieTrackerRepository.listDatesForUser(userId)
            .onItem().transform { Response.ok(it).build() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }

    @GET
    @Path("/list/{userId:\\d+}/{date}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<CalorieTrackerEntry>::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    fun listEntries(userId: Long, date: LocalDate): Uni<Response> {
        return calorieTrackerRepository.listEntriesForUserAndDate(userId, date)
            .onItem().transform { Response.ok(it).build() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }
}
