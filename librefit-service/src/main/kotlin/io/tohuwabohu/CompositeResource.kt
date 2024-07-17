package io.tohuwabohu

import io.quarkus.logging.Log
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.tohuwabohu.composite.Dashboard
import io.tohuwabohu.composite.Wizard
import io.tohuwabohu.crud.*
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.createErrorResponse
import jakarta.annotation.security.RolesAllowed
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

@Path("/api/composite")
class CompositeResource(
    private val userRepository: LibreUserRepository,
    private val foodCategoryRepository: FoodCategoryRepository,
    private val calorieTrackerRepository: CalorieTrackerRepository,
    private val calorieTargetRepository: CalorieTargetRepository,
    private val weightTrackerRepository: WeightTrackerRepository,
    private val weightTargetRepository: WeightTargetRepository
) {
    @GET
    @Path("/dashboard/{date}")
    @RolesAllowed("User", "Admin")
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(
            responseCode = "200", description = "OK", content = [Content(
                mediaType = "application/json", schema = Schema(implementation = Dashboard::class)
            )]
        ),
        APIResponse(
            responseCode = "400", description = "Bad Request", content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class)
            )]
        ),
        APIResponse(responseCode = "401", description = "Unauthorized"),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(
        operationId = "getDashboard"
    )
    fun getDashboard(@Context securityIdentity: SecurityIdentity, date: LocalDate): Uni<Response> {
        val userId = UUID.fromString(securityIdentity.principal.name)
        val week = date.minusWeeks(1)
        val month = date.minusMonths(1)

        return userRepository.findById(userId).chain { user ->
            foodCategoryRepository.listVisibleCategories().map { categories ->
                Dashboard(user.copy(password = ""), categories, null, emptyList(), emptyList(), null, emptyList(), emptyList())
            }
        }.chain { dash ->
            calorieTargetRepository.findLatestCalorieTarget(userId).map { target ->
                dash.copy(calorieTarget = target)
            }
        }.chain { dash ->
            calorieTrackerRepository.listEntriesForUserAndDate(userId, date).map { entries ->
                dash.copy(caloriesTodayList = entries)
            }
        }.chain { dash ->
            calorieTrackerRepository.listEntriesForUserAndDateRange(userId, week, date).map { entries ->
                dash.copy(caloriesWeekList = entries)
            }
        }.chain { dash ->
            weightTargetRepository.findLatestWeightTarget(userId).map { target ->
                dash.copy(weightTarget = target)
            }
        }.chain{ dash ->
            weightTrackerRepository.listEntriesForUserAndDate(userId, date).map { entries ->
                dash.copy(weightTodayList = entries)
            }

        }.chain { dash ->
            weightTrackerRepository.listEntriesForUserAndDateRange(userId, month, date).map { entries ->
                dash.copy(weightMonthList = entries)
            }
        }.onItem().transform { dash ->
            Response.ok(dash).build()
        }.onFailure().invoke { e -> Log.error(e) }.onFailure()
            .recoverWithItem { throwable -> createErrorResponse(throwable) }
    }

    @POST
    @Path("/wizard/result")
    @RolesAllowed("User", "Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        APIResponse(responseCode = "201", description = "Created"),
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
        operationId = "postWizardResult"
    )
    fun postWizardResult(
        @Context securityIdentity: SecurityIdentity,
        wizard: Wizard
    ): Uni<Response> {
        wizard.weightTarget.userId = UUID.fromString(securityIdentity.principal.name)
        wizard.calorieTarget.userId = UUID.fromString(securityIdentity.principal.name)
        wizard.weightTrackerEntry.userId = UUID.fromString(securityIdentity.principal.name)

        return calorieTargetRepository.validateAndPersist(wizard.calorieTarget)
            .chain { _ -> weightTargetRepository.validateAndPersist(wizard.weightTarget) }
            .chain { _ -> weightTrackerRepository.validateAndPersist(wizard.weightTrackerEntry) }
            .onItem().transform { _ ->
                Response.status(Response.Status.CREATED).build()
            }.onFailure().invoke { e -> Log.error(e) }.onFailure()
            .recoverWithItem { throwable -> createErrorResponse(throwable) }
    }
}