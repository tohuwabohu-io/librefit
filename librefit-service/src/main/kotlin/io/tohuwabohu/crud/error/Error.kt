package io.tohuwabohu.crud.error

import io.quarkus.logging.Log
import io.quarkus.security.ForbiddenException
import io.quarkus.security.UnauthorizedException
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.UniOnFailure
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.Logger
import java.time.format.DateTimeParseException

object ErrorHandler {
    @JvmStatic
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

    @JvmStatic
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

    @JvmStatic
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
}


data class ErrorDescription(val field: String, val message: String)
data class ErrorResponse(val title: String = "", val status: Int = 0, val errors: List<ErrorDescription> = emptyList())

class ValidationError(val errors: List<ErrorDescription>) : Throwable()

@Provider
class ValidationErrorMapper : ExceptionMapper<ValidationError> {

    private val log: Logger = Logger.getLogger(javaClass)

    override fun toResponse(exception: ValidationError): Response {
        log.error("Validation failed", exception)

        return Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse("Validation Error", 400, exception.errors)).build()
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

/**
 * Recovers from any failure of the [Uni] by creating an error response based on the type of the thrown exception.
 *
 * Usage: /*...*/.onFailure().recoverWithResponse()
 *
 * @return a [Uni] that emits a [Response] representing the recovered error response.
 */
fun UniOnFailure<Response>.recoverWithResponse(): Uni<Response> {
    return this.invoke { e -> Log.error("Recovering from Exception...", e) }
        .onFailure().recoverWithItem(ErrorHandler::createErrorResponse)
}