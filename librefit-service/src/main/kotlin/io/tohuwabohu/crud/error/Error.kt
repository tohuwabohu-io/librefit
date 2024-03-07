package io.tohuwabohu.crud.error

import io.quarkus.security.ForbiddenException
import io.quarkus.security.UnauthorizedException
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.Logger
import java.time.format.DateTimeParseException

fun createErrorResponse(throwable: Throwable): Response {
    return when (throwable) {
        is ValidationError -> {
            ValidationErrorMapper().toResponse(throwable)
        }

        is EntityNotFoundException -> {
            Response.status(Response.Status.NOT_FOUND).build()
        }

        is UnauthorizedException -> {
            UnauthorizedExceptionMapper().toResponse(throwable)
        }

        is ForbiddenException -> {
            Response.status(Response.Status.FORBIDDEN).build()
        }

        else -> {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
        }
    }
}

fun transformDateTimeParseException(t: DateTimeParseException, datePattern: String): ValidationError {
    return ValidationError(
        listOf(
            ErrorDescription(
                "datePattern",
                "Date '${t.parsedString}' in data received could not be parsed with '$datePattern' pattern."
            )
        )
    )
}

fun transformNumberFormatException(): ValidationError {
    return ValidationError(
        listOf(
            ErrorDescription(
                "amount",
                "Values in data received could not be parsed as number."
            )
        )
    )
}

class ErrorDescription(val field: String, val message: String)
class ErrorResponse(val errors: List<ErrorDescription>)
class ValidationError(val errors: List<ErrorDescription>) : Throwable()

@Provider
class ValidationErrorMapper : ExceptionMapper<ValidationError> {

    private val log: Logger = Logger.getLogger(javaClass)

    override fun toResponse(exception: ValidationError): Response {
        log.error("Validation failed", exception)

        return Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse(exception.errors)).build()
    }
}

@Provider
class UnauthorizedExceptionMapper: ExceptionMapper<UnauthorizedException> {
    private val log: Logger = Logger.getLogger(javaClass)

    override fun toResponse(exception: UnauthorizedException): Response {
        log.error("Authentication error", exception)

        return Response.status(Response.Status.UNAUTHORIZED).build()
    }
}