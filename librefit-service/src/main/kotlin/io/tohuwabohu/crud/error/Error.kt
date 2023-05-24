package io.tohuwabohu.crud.error

import io.quarkus.security.UnauthorizedException
import org.jboss.logging.Logger
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException
import javax.validation.ConstraintViolation
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

fun createErrorResponse(throwable: Throwable): Response {
    return when (throwable) {
        is ValidationError -> {
            ValidationErrorMapper().toResponse(throwable)
        }

        is EntityNotFoundException -> {
            Response.status(Response.Status.NOT_FOUND).build()
        }

        is NoResultException -> {
            Response.status(Response.Status.NOT_FOUND).build()
        }

        is UnmodifiedError -> {
            UnmodifiedErrorMapper().toResponse(throwable)
        }

        is UnauthorizedException -> {
            UnauthorizedExceptionMapper().toResponse(throwable)
        }

        else -> {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
        }
    }
}

class ErrorResponse(val messages: List<String>)
class ValidationError(val violations: List<String>) : Throwable()
class UnmodifiedError(override val message: String) : Throwable()

@Provider
class ValidationErrorMapper : ExceptionMapper<ValidationError> {

    private val log: Logger = Logger.getLogger(javaClass)

    override fun toResponse(exception: ValidationError): Response {
        log.error("Validation failed", exception)

        return Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse(exception.violations)).build()
    }
}

@Provider
class UnmodifiedErrorMapper : ExceptionMapper<UnmodifiedError> {
    private val log: Logger = Logger.getLogger(javaClass)

    override fun toResponse(exception: UnmodifiedError): Response {
        log.error("Update statement has been issued but no data updated", exception)

        return Response.status(Response.Status.NOT_MODIFIED).build()
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