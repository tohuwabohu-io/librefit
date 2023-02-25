package io.tohuwabohu

import io.tohuwabohu.calc.Limit
import io.tohuwabohu.calc.Tdee
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/tdee")
class TdeeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/calculate/{age:\\d+}/{sex}/{weight:\\d+}/{height:\\d+}/{activityLevel}")
    fun calculate(age: Int, sex: String, weight: Int, height: Int, activityLevel: Float) = Tdee(age, sex, weight, height, activityLevel)

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/calculate/{tdee:\\d+}/{bmr:\\d+}/{diff:\\d+}/{gain}")
    fun calculate(tdee: Float, bmr: Float, diff: Float, gain: String) = Limit(tdee, bmr, diff, gain == "true")
}