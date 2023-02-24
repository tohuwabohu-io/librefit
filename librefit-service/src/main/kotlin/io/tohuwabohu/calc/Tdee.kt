package io.tohuwabohu.calc

import kotlin.math.round

/**
 * Calculation of basic metabolic rate (BMR) and total daily energy expenditure (TDEE) based on the Harris-Benedict
 * formula.
 */
data class Tdee (val age: Number, val sex: String, val weight: Number, val height: Number, val activityLevel: Number) {
    val bmr = if (sex == "m") {
        round(66 + (13.7 * weight.toFloat()) + (5 * height.toFloat()) - (6.8 * age.toFloat()))
    } else {
        round(655 + (9.6 * weight.toFloat()) + (1.8 * height.toFloat()) - (4.7 * age.toFloat()))
    }

    val tdee = round(activityLevel.toFloat() * bmr)
}

