package io.tohuwabohu.calc

import kotlin.math.pow
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
    }

    val tdee = round(activityLevel.toFloat() * bmr)

    val deficit = weeklyDifference.toFloat() / 10 * 7000 / 7

    val target: Float = when (calculationGoal) {
        CalculationGoal.GAIN -> {
            tdee.toFloat() + deficit
        }

        CalculationGoal.LOSS -> {
            tdee.toFloat() - deficit;
        }
    }

    val bmi: Float = round(weight.toFloat() / ((height.toFloat() / 100).pow(2)))

    val bmiCategory: BmiCategory  = when (sex) {
        CalculationSex.FEMALE -> {
            when (bmi) {
                in 0f..18f -> BmiCategory.UNDERWEIGHT
                in 19f..24f -> BmiCategory.STANDARD_WEIGHT
                in 25f..30f -> BmiCategory.OVERWEIGHT
                in 31f..40f -> BmiCategory.OBESE
                else -> BmiCategory.SEVERELY_OBESE
            }
        }

        CalculationSex.MALE -> {
            when (bmi) {
                in 0f..19f -> BmiCategory.UNDERWEIGHT
                in 20f..25f -> BmiCategory.STANDARD_WEIGHT
                in 26f..30f -> BmiCategory.OVERWEIGHT
                in 31f..40f -> BmiCategory.OBESE
                else -> BmiCategory.SEVERELY_OBESE
            }
        }
    }

    val targetBmi = when (age) {
        in 19..24 -> arrayOf(19,24)
        in 25..34 -> arrayOf(20,25)
        in 35..44 -> arrayOf(21,26)
        in 45..54 -> arrayOf(22,27)
        in 55..64 -> arrayOf(23,28)
        else -> arrayOf(24,29)
    }

    val targetWeight = round(((targetBmi[0] + targetBmi[1]).toFloat() / 2) * (height.toFloat() / 100).pow(2))

    val durationDays = when (calculationGoal) {
        CalculationGoal.GAIN -> {
            (targetWeight - weight.toFloat()) * 7000 / deficit
        }

        CalculationGoal.LOSS -> {
            (weight.toFloat() - targetWeight) * 7000 / deficit
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

enum class BmiCategory {
    UNDERWEIGHT,
    STANDARD_WEIGHT,
    OVERWEIGHT,
    OBESE,
    SEVERELY_OBESE,
}