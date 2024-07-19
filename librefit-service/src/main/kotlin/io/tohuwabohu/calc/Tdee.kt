package io.tohuwabohu.calc

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.error.ErrorDescription
import io.tohuwabohu.crud.error.ValidationError
import io.tohuwabohu.crud.util.OneOfFloat
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.validation.Validator
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import kotlin.math.pow
import kotlin.math.round


@ApplicationScoped
class TdeeCalculator {
    @Inject
    private lateinit var validator: Validator

    fun validate(tdee: Tdee) {
        val violations = validator.validate(tdee)

        if (violations.isNotEmpty()) {
            val errors = violations.map { violation ->
                ErrorDescription(violation.propertyPath.filterNotNull()[0].name, violation.message)
            }

            throw ValidationError(errors)
        }
    }

    fun calculate(tdee: Tdee): Uni<Tdee> {
        validate(tdee)

        tdee.bmr = when (tdee.sex) {
            CalculationSex.MALE -> {
                round(66 + (13.7 * tdee.weight.toFloat()) + (5 * tdee.height.toFloat()) - (6.8 * tdee.age.toFloat())).toFloat()
            }

            CalculationSex.FEMALE -> {
                round(655 + (9.6 * tdee.weight.toFloat()) + (1.8 * tdee.height.toFloat()) - (4.7 * tdee.age.toFloat())).toFloat()
            }
        }

        tdee.tdee = round(tdee.activityLevel * tdee.bmr)

        tdee.deficit = tdee.weeklyDifference.toFloat() / 10 * 7000 / 7

        tdee.target = when (tdee.calculationGoal) {
            CalculationGoal.GAIN -> {
                tdee.tdee.toFloat() + tdee.deficit
            }

            CalculationGoal.LOSS -> {
                tdee.tdee.toFloat() - tdee.deficit;
            }
        }

        tdee.bmi = round(tdee.weight.toFloat() / ((tdee.height.toFloat() / 100).pow(2)))

        tdee.bmiCategory  = when (tdee.sex) {
            CalculationSex.FEMALE -> {
                when (tdee.bmi) {
                    in 0f..18f -> BmiCategory.UNDERWEIGHT
                    in 19f..24f -> BmiCategory.STANDARD_WEIGHT
                    in 25f..30f -> BmiCategory.OVERWEIGHT
                    in 31f..40f -> BmiCategory.OBESE
                    else -> BmiCategory.SEVERELY_OBESE
                }
            }

            CalculationSex.MALE -> {
                when (tdee.bmi) {
                    in 0f..19f -> BmiCategory.UNDERWEIGHT
                    in 20f..25f -> BmiCategory.STANDARD_WEIGHT
                    in 26f..30f -> BmiCategory.OVERWEIGHT
                    in 31f..40f -> BmiCategory.OBESE
                    else -> BmiCategory.SEVERELY_OBESE
                }
            }
        }

        tdee.targetBmi = when (tdee.age) {
            in 19..24 -> arrayOf(19,24)
            in 25..34 -> arrayOf(20,25)
            in 35..44 -> arrayOf(21,26)
            in 45..54 -> arrayOf(22,27)
            in 55..64 -> arrayOf(23,28)
            else -> arrayOf(24,29)
        }

        tdee.targetWeight = round(((tdee.targetBmi[0] + tdee.targetBmi[1]).toFloat() / 2) * (tdee.height.toFloat() / 100).pow(2))
        tdee.targetWeightLower = round(((tdee.targetBmi[0]).toFloat()) * (tdee.height.toFloat() / 100).pow(2))
        tdee.targetWeightUpper = round(((tdee.targetBmi[1]).toFloat()) * (tdee.height.toFloat() / 100).pow(2))

        tdee.durationDays = when (tdee.calculationGoal) {
            CalculationGoal.GAIN -> {
                (tdee.targetWeight - tdee.weight.toFloat()) * 7000 / tdee.deficit
            }

            CalculationGoal.LOSS -> {
                (tdee.weight.toFloat() - tdee.targetWeight) * 7000 / tdee.deficit
            }
        }


        return Uni.createFrom().item(tdee)
    }
}

/**
 * Calculation of basic metabolic rate (BMR) and total daily energy expenditure (TDEE) based on the Harris-Benedict
 * formula.
 */
data class Tdee(
    @field:Min(value = 18, message = "Your age must be between 18 and 99 years.")
    @field:Max(value = 99, message = "Your age must be between 18 and 99 years.")
    val age: Number,

    val sex: CalculationSex,

    @field:Min(value = 30, message = "Please provide a weight between 30kg and 300kg.")
    @field:Max(value = 300, message = "Please provide a weight between 30kg and 300kg.")
    val weight: Number,

    @field:Min(value = 30, message = "Please provide a height between 100cm and 220cm.")
    @field:Max(value = 300, message = "Please provide a height between 100cm and 220cm.")
    val height: Number,

    @OneOfFloat(value = [1f, 1.25f, 1.5f, 1.75f, 2f], message = "Please enter a valid activity level.")
    val activityLevel: Float,

    @field:Min(value = 0, message = "Your weekly difference must be between 0 and 0.7kg.")
    @field:Max(value = 7, message = "Your weekly difference must be between 0 and 0.7kg.")
    val weeklyDifference: Number,
    val calculationGoal: CalculationGoal,
    var bmr: Float = 0f,
    var tdee: Number = 0,
    var deficit: Float = 0f,
    var target: Float = 0f,
    var bmi: Float = 0f,
    var bmiCategory: BmiCategory = BmiCategory.STANDARD_WEIGHT,
    var targetBmi: Array<Int> = arrayOf(),
    var targetWeight: Float = 0f,
    var targetWeightUpper: Float = 0f,
    var targetWeightLower: Float = 0f,
    var durationDays: Number = 0
)

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