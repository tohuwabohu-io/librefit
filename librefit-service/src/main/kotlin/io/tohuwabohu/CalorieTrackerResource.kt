package io.tohuwabohu

import io.quarkus.logging.Log
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.*
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.recoverWithResponse
import io.tohuwabohu.crud.user.LibreUserSecurity
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import java.util.*

@Path("/api/tracker/calories")
@RequestScoped
class CalorieTrackerResource(
    private val calorieTrackerRepository: CalorieTrackerRepository,
    private val foodCategoryRepository: FoodCategoryRepository
) {
    @POST
    @Path("/create")
    @RolesAllowed("User", "Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "201", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CalorieTracker::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "createCalorieTracker")
    fun create(@Context securityIdentity: SecurityIdentity, @Valid calorieTracker: CalorieTracker): Uni<Response> {
        Log.info("Creating a new calorie tracker entry=$calorieTracker")

        return calorieTrackerRepository.validateAndPersist(LibreUserSecurity.withPrincipal(securityIdentity, calorieTracker))
            .onItem().transform { entry -> Response.ok(entry).status(Response.Status.CREATED).build() }
            .onFailure().recoverWithResponse()
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
    @Operation(operationId = "updateCalorieTracker")
    fun update(@Context securityIdentity: SecurityIdentity, @Valid calorieTracker: CalorieTracker): Uni<Response> {
        Log.info("Updating calorie tracker entry $calorieTracker")

        return calorieTrackerRepository.updateEntry(LibreUserSecurity.withPrincipal(securityIdentity, calorieTracker), CalorieTracker::class.java)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().recoverWithResponse()
    }


    @GET
    @Path("/read/{date}/{sequence:\\d+}")
    @RolesAllowed("User", "Admin")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CalorieTracker::class)
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
    @Operation(operationId = "readCalorieTracker")
    fun read(@Context securityIdentity: SecurityIdentity, date: LocalDate, sequence: Long): Uni<Response> {
        return calorieTrackerRepository.readEntry(UUID.fromString(securityIdentity.principal.name), date, sequence)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().recoverWithResponse()
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
    @Operation(operationId = "deleteCalorieTracker")
    fun delete(@Context securityIdentity: SecurityIdentity, date: LocalDate, sequence: Long): Uni<Response> {
        Log.info("Delete calorie tracker entry with added=$date sequence=$sequence")

        return calorieTrackerRepository.deleteEntry(UUID.fromString(securityIdentity.principal.name), date, sequence)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.serverError().build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/list/dates/{dateFrom}/{dateTo}")
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
    @Operation(operationId = "listCalorieTrackerDatesRange")
    fun listDates(@Context securityIdentity: SecurityIdentity, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> {
        return calorieTrackerRepository.listDatesForUser(UUID.fromString(securityIdentity.principal.name), dateFrom, dateTo)
            .onItem().transform { Response.ok(it).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/list/{date}")
    @RolesAllowed("User", "Admin")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<CalorieTracker>::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "listCalorieTrackerForDate")
    fun listEntries(@Context securityIdentity: SecurityIdentity, date: LocalDate): Uni<Response> {
        return calorieTrackerRepository.listEntriesForUserAndDate(UUID.fromString(securityIdentity.principal.name), date)
            .onItem().transform { Response.ok(it).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/list/{dateFrom}/{dateTo}")
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<CalorieTracker>::class)
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
        operationId = "listCalorieTrackerRange"
    )
    fun listEntries(@Context securityIdentity: SecurityIdentity, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> {
        return calorieTrackerRepository.listEntriesForUserAndDateRange(UUID.fromString(securityIdentity.principal.name), dateFrom, dateTo)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/categories/list")
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<FoodCategory>::class)
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
        operationId = "listFoodCategories"
    )
    fun listCategories(): Uni<Response> {
        return foodCategoryRepository.listVisibleCategories()
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().recoverWithResponse()
    }
}
