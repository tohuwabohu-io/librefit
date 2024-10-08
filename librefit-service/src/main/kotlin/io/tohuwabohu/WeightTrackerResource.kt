package io.tohuwabohu

import io.quarkus.logging.Log
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.WeightTracker
import io.tohuwabohu.crud.WeightTrackerRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.recoverWithResponse
import io.tohuwabohu.crud.user.LibreUserSecurity
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import java.util.*

@Path("/api/tracker/weight")
class WeightTrackerResource(private val weightTrackerRepository: WeightTrackerRepository) {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "201", description = "Created", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WeightTracker::class)
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
        operationId = "createWeightTracker"
    )
    fun create(@Context securityIdentity: SecurityIdentity, @Valid weightTracker: WeightTracker): Uni<Response> {
        Log.info("Creating a new weight tracker entry=$weightTracker")

        return weightTrackerRepository.validateAndPersist(LibreUserSecurity.withPrincipal(securityIdentity, weightTracker))
            .onItem().transform { entry -> Response.ok(entry).status(Response.Status.CREATED).build() }
            .onFailure().recoverWithResponse()
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
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
        operationId = "updateWeightTracker"
    )
    fun update(@Context securityIdentity: SecurityIdentity, @Valid weightTracker: WeightTracker): Uni<Response> {
        Log.info("Updating weight tracker entry $weightTracker")

        return weightTrackerRepository.updateEntry(LibreUserSecurity.withPrincipal(securityIdentity, weightTracker), WeightTracker::class.java)
            .onItem().transform { updated -> Response.ok(updated).build() }
            .onFailure().recoverWithResponse()
    }

    @DELETE
    @Path("/delete/{date}/{sequence:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
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
        operationId = "deleteWeightTracker"
    )
    fun delete(@Context securityIdentity: SecurityIdentity, date: LocalDate, sequence: Long): Uni<Response> {
        Log.info("Delete weight tracker entry with added=$date sequence=$sequence")

        return weightTrackerRepository.deleteEntry(UUID.fromString(securityIdentity.principal.name), date, sequence)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.serverError().build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/read/{date}/{sequence:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WeightTracker::class)
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
        operationId = "readWeightTracker"
    )
    fun read(@Context securityIdentity: SecurityIdentity, date: LocalDate, sequence: Long): Uni<Response> =
        weightTrackerRepository.readEntry(UUID.fromString(securityIdentity.principal.name), date, sequence)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().recoverWithResponse()

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
    @Operation(operationId = "listWeightTrackerDatesRange")
    fun listDates(@Context securityIdentity: SecurityIdentity, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> {

        return weightTrackerRepository.listDatesForUser(UUID.fromString(securityIdentity.principal.name), dateFrom, dateTo)
            .onItem().transform { Response.ok(it).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/list/{date}")
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<WeightTracker>::class)
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
        operationId = "listWeightTracker"
    )
    fun listEntries(@Context securityIdentity: SecurityIdentity, date: LocalDate): Uni<Response> {
        return weightTrackerRepository.listEntriesForUserAndDate(UUID.fromString(securityIdentity.principal.name), date)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/list/{dateFrom}/{dateTo}")
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Array<WeightTracker>::class)
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
        operationId = "listWeightTrackerRange"
    )
    fun listEntries(@Context securityIdentity: SecurityIdentity, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> {
        return weightTrackerRepository.listEntriesForUserAndDateRange(UUID.fromString(securityIdentity.principal.name), dateFrom, dateTo)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Path("/last")
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WeightTracker::class)
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
        operationId = "findLastWeightTracker"
    )
    fun findLast(@Context securityIdentity: SecurityIdentity, @Context securityContext: SecurityContext): Uni<Response> = weightTrackerRepository
        .findLastEntry(UUID.fromString(securityIdentity.principal.name))
        .onItem().transform { entry -> Response.ok(entry).build() }
        .onFailure().recoverWithResponse()
}