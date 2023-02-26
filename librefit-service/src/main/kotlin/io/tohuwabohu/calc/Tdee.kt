package io.tohuwabohu.calc

import kotlin.math.round

/**
 * Calculation of basic metabolic rate (BMR) and total daily energy expenditure (TDEE) based on the Harris-Benedict
 * formula.
 */
data class Tdee(
    val age: Number,
    val sex: CalculationSex,
    val weight: Number,
    val height: Number,
    val activityLevel: Number,
    val weeklyDifference: Number,
    val calculationGoal: CalculationGoal
) {
    val bmr = when (sex) {
        CalculationSex.MALE -> {
            round(66 + (13.7 * weight.toFloat()) + (5 * height.toFloat()) - (6.8 * age.toFloat()))
        }

        CalculationSex.FEMALE -> {
            round(655 + (9.6 * weight.toFloat()) + (1.8 * height.toFloat()) - (4.7 * age.toFloat()))
        }

        else -> {
            0.0
        }
    }

    val tdee = round(activityLevel.toFloat() * bmr)

    val deficit = weeklyDifference.toFloat() * 7000 / 7

    val target: Float = when (calculationGoal) {
        CalculationGoal.GAIN -> {
            tdee.toFloat() + deficit
        }

        CalculationGoal.LOSS -> {
            tdee.toFloat() - deficit;
        }

        else -> {
            0.0f
        }
    }
}

enum class CalculationGoal {
    GAIN,
    LOSS
}

enum class CalculationSex {
    MALE,
    FEMALE
}