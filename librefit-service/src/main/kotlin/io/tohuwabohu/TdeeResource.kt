package io.tohuwabohu

import io.tohuwabohu.calc.CalculationGoal
import io.tohuwabohu.calc.CalculationSex
import io.tohuwabohu.calc.Tdee
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/tdee")
class TdeeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/calculate/{age:\\d+}/{sex}/{weight:\\d+}/{height:\\d+}/{activityLevel}/{diff}/{gain}")
    fun calculate(
        age: Int,
        sex: CalculationSex,
        weight: Int,
        height: Int,
        activityLevel: Float,
        diff: Float,
        gain: CalculationGoal
    ) = Tdee(age, sex, weight, height, activityLevel, diff, gain)
}