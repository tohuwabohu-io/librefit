package io.tohuwabohu

import io.tohuwabohu.calc.CalculationGoal
import io.tohuwabohu.calc.CalculationSex
import io.tohuwabohu.calc.Tdee
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/tdee")
class TdeeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/calculate/{age:\\d+}/{sex}/{weight:\\d+}/{height:\\d+}/{activityLevel}/{weeklyDifference}/{calculationGoal}")
    @APIResponses(
        APIResponse(responseCode = "200", description = "OK", content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Tdee::class)
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
    ) = Tdee(age, sex, weight, height, activityLevel, weeklyDifference, calculationGoal)
}