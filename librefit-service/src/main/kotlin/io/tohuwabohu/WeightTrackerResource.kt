package io.tohuwabohu

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.WeightTrackerEntry
import io.tohuwabohu.crud.WeightTrackerRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import io.tohuwabohu.security.printAuthenticationInfo
import io.tohuwabohu.security.validateToken
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Path("/tracker/weight")
class WeightTrackerResource(val weightTrackerRepository: WeightTrackerRepository) {
    @Inject
    lateinit var jwt: JsonWebToken

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "createWeightTrackerEntry"
    )
    fun create(@Context securityContext: SecurityContext, @Valid weightTrackerEntry: WeightTrackerEntry): Uni<Response> {
        Log.info("Creating a new weight tracker entry=$weightTrackerEntry")

        weightTrackerEntry.userId = jwt.name.toLong()

        printAuthenticationInfo(jwt, securityContext)
        validateToken(jwt, weightTrackerEntry)

        return weightTrackerRepository.validateAndPersist(weightTrackerEntry)
            .onItem().transform { entry -> Response.ok(entry).status(Response.Status.CREATED).entity(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{throwable -> createErrorResponse(throwable) }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "304", description = "Not Modified"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "updateWeightTrackerEntry"
    )
    fun update(@Context securityContext: SecurityContext, @Valid weightTrackerEntry: WeightTrackerEntry): Uni<Response> {
        Log.info("Updating weight tracker entry $weightTrackerEntry")

        printAuthenticationInfo(jwt, securityContext)
        validateToken(jwt, weightTrackerEntry)

        return weightTrackerRepository.updateTrackingEntry(weightTrackerEntry)
            .onItem().transform { rowCount -> if (rowCount > 0) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke{ e -> Log.error(e)}
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @DELETE
    @Path("/delete/{date}/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "304", description = "Not Modified"),
        APIResponse(responseCode = "404", description = "Not Found"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "deleteWeightTrackerEntry"
    )
    fun delete(@Context securityContext: SecurityContext, date: LocalDate, id: Long): Uni<Response> {
         Log.info("Delete weight tracker entry with added=$date id=$id")

        printAuthenticationInfo(jwt, securityContext)

        return weightTrackerRepository.deleteEntry(jwt.name.toLong(), date, id)
            .onItem().transform { deleted -> if (deleted == true) Response.ok().build() else Response.notModified().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem {throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/read/{date}/{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "readWeightTrackerEntry"
    )
    fun read(@Context securityContext: SecurityContext, date: LocalDate, id: Long): Uni<Response> =
        weightTrackerRepository.readEntry(jwt.name.toLong(), date, id)
            .onItem().transform { entry -> Response.ok(entry).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }

    @GET
    @Path("/list/{date}")
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "listWeightTrackerEntries"
    )
    fun listEntries(@Context securityContext: SecurityContext, date: LocalDate): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        return weightTrackerRepository.listEntriesForUserAndDate(jwt.name.toLong(), date)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/list/{dateFrom}/{dateTo}")
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "listWeightTrackerEntriesRange"
    )
    fun listEntries(@Context securityContext: SecurityContext, dateFrom: LocalDate, dateTo: LocalDate): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        return weightTrackerRepository.listEntriesForUserAndDateRange(jwt.name.toLong(), dateFrom, dateTo)
            .onItem().transform { list -> Response.ok(list).build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @GET
    @Path("/last")
    @RolesAllowed("User", "Admin")
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
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        operationId = "findLastWeightTrackerEntry"
    )
    fun findLast(@Context securityContext: SecurityContext): Uni<Response> = weightTrackerRepository.findLastEntry(jwt.name.toLong())
        .onItem().transform { entry -> Response.ok(entry).build() }
        .onFailure().invoke { e -> Log.error(e) }
        .onFailure().recoverWithItem{ throwable -> createErrorResponse(throwable) }
}