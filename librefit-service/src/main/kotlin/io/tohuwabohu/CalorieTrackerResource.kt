package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.CalorieTrackerRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import io.tohuwabohu.security.printAuthenticationInfo
import io.tohuwabohu.security.validateToken
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
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

@Path("/tracker/calories")
@RequestScoped
class CalorieTrackerResource(val calorieTrackerRepository: CalorieTrackerRepository) {
    @Inject
    lateinit var jwt: JsonWebToken

    @POST
    @Path("/create")
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "createCalorieTrackerEntry")
    fun create(@Context securityContext: SecurityContext, @Valid calorieTracker: CalorieTrackerEntry): Uni<Response> {
        calorieTracker.userId = UUID.fromString(jwt.name)

        Log.info("Creating a new calorie tracker entry=$calorieTracker")

        printAuthenticationInfo(jwt, securityContext)
        validateToken(jwt, calorieTracker)

        return calorieTrackerRepository.validateAndPersist(calorieTracker)
            .onItem().transform { entry -> Response.ok(entry).status(Response.Status.CREATED).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @PUT
    @Path("/update")
    @RolesAllowed("User", "Admin")
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
    @Operation(operationId = "updateCalorieTrackerEntry")
    fun update(@Context securityContext: SecurityContext, @Valid calorieTracker: CalorieTrackerEntry): Uni<Response> {
        Log.info("Updating calorie tracker entry $calorieTracker")

        printAuthenticationInfo(jwt, securityContext)
        validateToken(jwt, calorieTracker)

        return calorieTrackerRepository.updateEntry(calorieTracker, CalorieTrackerEntry::class.java)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }


    @GET
    @Path("/read/{date}/{sequence:\\d+}")
    @RolesAllowed("User", "Admin")
    @Produces(MediaType.APPLICATION_JSON)
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "readCalorieTrackerEntry")
    fun read(@Context securityContext: SecurityContext, date: LocalDate, sequence: Long): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        return calorieTrackerRepository.readEntry(UUID.fromString(jwt.name), date, sequence)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }


    @DELETE
    @Path("/delete/{date}/{sequence:\\d+}")
    @RolesAllowed("User", "Admin")
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
    @Operation(operationId = "deleteCalorieTrackerEntry")
    fun delete(@Context securityContext: SecurityContext, date: LocalDate, sequence: Long): Uni<Response> {
        Log.info("Delete calorie tracker entry with added=$date sequence=$sequence")
        printAuthenticationInfo(jwt, securityContext)

        return calorieTrackerRepository.deleteEntry(UUID.fromString(jwt.name), date, sequence)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.serverError().build() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/list/dates")
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "listCalorieTrackerDates")
    fun listDates(@Context securityContext: SecurityContext): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        return calorieTrackerRepository.listDatesForUser(UUID.fromString(jwt.name))
            .onItem().transform { Response.ok(it).build() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/list/{date}")
    @RolesAllowed("User", "Admin")
    @Produces(MediaType.APPLICATION_JSON)
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "listCalorieTrackerEntriesForDate")
    fun listEntries(@Context securityContext: SecurityContext, date: LocalDate): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        return calorieTrackerRepository.listEntriesForUserAndDate(UUID.fromString(jwt.name), date)
            .onItem().transform { Response.ok(it).build() }
            .onFailure().invoke { throwable -> Log.error(throwable) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/list/{dateFrom}/{dateTo}")
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "listCalorieTrackerEntriesRange"
    )
    fun listEntries(@Context securityContext: SecurityContext, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        return calorieTrackerRepository.listEntriesForUserAndDateRange(UUID.fromString(jwt.name), dateFrom, dateTo)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }
}
