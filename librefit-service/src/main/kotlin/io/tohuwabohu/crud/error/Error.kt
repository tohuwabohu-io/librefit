package io.tohuwabohu.crud.error

import io.tohuwabohu.crud.validation.ValidationError
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

data class ErrorResponse(val message: String)

@Provider
class ValidationErrorMapper : ExceptionMapper<ValidationError> {
    override fun toResponse(exception: ValidationError): Response {
        return Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse(exception.message)).build()
    }

}