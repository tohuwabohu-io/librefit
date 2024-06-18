package io.tohuwabohu;

import io.quarkus.logging.Log
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.ImportConfig
import io.tohuwabohu.crud.ImportHelper
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import io.tohuwabohu.crud.error.transformDateTimeParseException
import io.tohuwabohu.crud.error.transformNumberFormatException
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.jboss.resteasy.reactive.PartType
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.time.format.DateTimeParseException
import java.util.*

@Path("/api/import")
@RequestScoped
class ImportResource(private val importHelper: ImportHelper) {
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
    @Operation(operationId = "postImportBulk")
    fun bulk(@Context securityIdentity: SecurityIdentity,
             @RestForm @PartType("text/csv") fileName: String,
             @RestForm @PartType("application/json") config: ImportConfig,
             @RestForm @PartType("application/octet-stream") file: FileUpload
    ): Uni<Response> {
        val userId = UUID.fromString(securityIdentity.principal.name)

        return importHelper.readCsv(file.uploadedFile().toFile(), config)
            .chain { csv -> importHelper.import(userId, csv) }
        .onItem().transform { _ -> Response.ok().build() }
            .onFailure().invoke { e -> Log.error(e) }
            .onFailure(DateTimeParseException::class.java)
                .transform { t -> transformDateTimeParseException(t as DateTimeParseException, config.datePattern) }
            .onFailure(NumberFormatException::class.java)
                .transform { _ -> transformNumberFormatException() }
            .onFailure().recoverWithItem { throwable -> createErrorResponse(throwable) }

    }
}
