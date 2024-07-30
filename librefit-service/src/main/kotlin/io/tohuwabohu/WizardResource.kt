package io.tohuwabohu

import io.smallrye.mutiny.Uni
import io.tohuwabohu.calc.CalculationGoal
import io.tohuwabohu.calc.CalculationSex
import io.tohuwabohu.calc.Wizard
import io.tohuwabohu.calc.WizardInput
import io.tohuwabohu.calc.WizardTargetDateResult
import io.tohuwabohu.crud.error.ErrorResponse
import io.tohuwabohu.crud.error.recoverWithResponse
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.time.LocalDate

@Path("/api/wizard")
class WizardResource(private val calculator: Wizard) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/calculate/{age:\\d+}/{sex}/{weight:\\d+}/{height:\\d+}/{activityLevel}/{weeklyDifference:\\d+}/{calculationGoal}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WizardInput::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class)
            )
        ]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "calculateTdee")
    fun calculate(
        age: Int,
        sex: CalculationSex,
        weight: Int,
        height: Int,
        activityLevel: Float,
        weeklyDifference: Float,
        calculationGoal: CalculationGoal
    ): Uni<Response> {
        return calculator.calculate(WizardInput(age, sex, weight, height, activityLevel, weeklyDifference, calculationGoal))
            .onItem().transform { result -> Response.ok(result).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/custom/date/{age:\\d+}/{height:\\d+}/{weight:\\d+}/{sex}/{targetDate}/{calculationGoal}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WizardTargetDateResult::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class)
            )
        ]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "calculateForTargetDate")
    fun calculateForTargetDate(
        age: Int,
        height: Int,
        weight: Int,
        sex: CalculationSex,
        targetDate: LocalDate,
        calculationGoal: CalculationGoal
    ): Uni<Response> {
        return calculator.calculateForTargetDate(age, height, weight.toFloat(), sex, targetDate, calculationGoal)
            .onItem().transform { result -> Response.ok(result).build() }
            .onFailure().recoverWithResponse()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/custom/weight/{age:\\d+}/{height:\\d+}/{weight:\\d+}/{sex}/{targetWeight}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = WizardTargetDateResult::class)
            )
        ]),
        APIResponse(responseCode = "400", description = "Bad Request", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class)
            )
        ]),
        APIResponse(responseCode = "500", description = "Internal Server Error")
    )
    @Operation(operationId = "calculateForTargetWeight")
    fun calculateForTargetWeight(
        age: Int,
        height: Int,
        weight: Int,
        sex: CalculationSex,
        targetWeight: Int
    ): Uni<Response> {
        return calculator.calculateForTargetWeight(LocalDate.now(), age, height, weight, sex, targetWeight)
            .onItem().transform { result -> Response.ok(result).build() }
            .onFailure().recoverWithResponse()
    }
}

