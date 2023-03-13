package io.tohuwabohu.crud.error

import io.tohuwabohu.crud.validation.ValidationError
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.jboss.logging.Logger
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

fun createErrorResponse(throwable: Throwable): Response {
    return if (throwable is ValidationError) {
        Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse(throwable.message)).build()
    } else {
        Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
    }
}

class ErrorResponse(val message: String)

@Provider
class ValidationErrorMapper : ExceptionMapper<ValidationError> {
    private val log: Logger = Logger.getLogger(javaClass)

    override fun toResponse(exception: ValidationError): Response {
        log.error("Validation failed", exception)

        return Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse(exception.message)).build()
    }

}