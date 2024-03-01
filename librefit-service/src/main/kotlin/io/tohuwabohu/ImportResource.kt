package io.tohuwabohu;

import io.quarkus.logging.Log
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.CalorieTrackerRepository
import io.tohuwabohu.crud.WeightTrackerRepository
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import io.tohuwabohu.csv.ImportConfig
import io.tohuwabohu.csv.collectCalorieTrackerEntries
import io.tohuwabohu.csv.collectWeightEntries
import io.tohuwabohu.csv.readCsv
import io.tohuwabohu.security.printAuthenticationInfo
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.jboss.resteasy.reactive.PartType
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.util.*

@Path("/import")
@RequestScoped
class ImportResource(val weightTrackerRepository: WeightTrackerRepository, val calorieTrackerRepository: CalorieTrackerRepository) {

    @Inject
    private lateinit var jwt: JsonWebToken

    @POST
    @Path("/bulk")
    @RolesAllowed("User", "Admin")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK"),
        APIResponse(responseCode = "400", description = "Bad Request", content = [ Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class)
        )]),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    fun bulk(@Context securityContext: SecurityContext,
             @RestForm fileName: String,
             @RestForm @PartType("application/json") config: ImportConfig,
             @RestForm @PartType("application/octet-stream") file: FileUpload
    ): Uni<Response> {
        printAuthenticationInfo(jwt, securityContext)

        val userId = UUID.fromString(jwt.name)

        return readCsv(file.uploadedFile().toFile(), config).chain { csv ->
            collectCalorieTrackerEntries(userId, csv).chain(calorieTrackerRepository::importBulk)
                .chain { _ -> collectWeightEntries(userId, csv).chain(weightTrackerRepository::importBulk)}
        }.onItem().transform { _ -> Response.ok().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }
    }
}
