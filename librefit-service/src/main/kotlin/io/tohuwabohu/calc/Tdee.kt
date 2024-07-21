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
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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

            null -> tdee.tdee as Float
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
            in 19..24 -> listOf(19,24)
            in 25..34 -> listOf(20,25)
            in 35..44 -> listOf(21,26)
            in 45..54 -> listOf(22,27)
            in 55..64 -> listOf(23,28)
            else -> listOf(24,29)
        }

        tdee.targetWeight = round(((tdee.targetBmi[0] + tdee.targetBmi[1]).toFloat() / 2) * (tdee.height.toFloat() / 100).pow(2))
        tdee.targetWeightLower = round(((tdee.targetBmi[0]).toFloat()) * (tdee.height.toFloat() / 100).pow(2))
        tdee.targetWeightUpper = round(((tdee.targetBmi[1]).toFloat()) * (tdee.height.toFloat() / 100).pow(2))

        when (tdee.calculationGoal) {
            CalculationGoal.GAIN -> {
                tdee.durationDays = (tdee.targetWeight - tdee.weight.toFloat()) * 7000 / tdee.deficit
                tdee.durationDaysUpper = (tdee.targetWeightUpper - tdee.weight.toFloat()) * 7000 / tdee.deficit
                tdee.durationDaysLower = (tdee.targetWeightLower - tdee.weight.toFloat()) * 7000 / tdee.deficit
            }

            CalculationGoal.LOSS -> {
                tdee.durationDays = (tdee.weight.toFloat() - tdee.targetWeight) * 7000 / tdee.deficit
                tdee.durationDaysUpper = (tdee.weight.toFloat() - tdee.targetWeightUpper) * 7000 / tdee.deficit
                tdee.durationDaysLower = (tdee.weight.toFloat() - tdee.targetWeightLower) * 7000 / tdee.deficit
            }

            null -> {
                tdee.durationDays = 0
                tdee.durationDaysLower = 0
                tdee.durationDaysUpper = 0
            }
        }

        tdee.recommendation = when (tdee.bmiCategory) {
            BmiCategory.STANDARD_WEIGHT -> WizardRecommendation.HOLD
            BmiCategory.UNDERWEIGHT -> WizardRecommendation.GAIN
            BmiCategory.OVERWEIGHT -> WizardRecommendation.LOSE
            BmiCategory.OBESE -> WizardRecommendation.LOSE
            BmiCategory.SEVERELY_OBESE -> WizardRecommendation.LOSE
        }

        return Uni.createFrom().item(tdee)
    }

    fun calculateForTargetDate(weight: Float, targetDate: LocalDate): Uni<WizardCustomization> {
        val today = LocalDate.now()

        val daysBetween = ChronoUnit.DAYS.between(today, targetDate)

        val targetWeightPerRate = mapOf(
            Pair(100, weight + (daysBetween * 100 / 7000)),
            Pair(200, weight + (daysBetween * 200 / 7000)),
            Pair(300, weight + (daysBetween * 300 / 7000)),
            Pair(400, weight + (daysBetween * 400 / 7000)),
            Pair(500, weight + (daysBetween * 500 / 7000)),
            Pair(600, weight + (daysBetween * 600 / 7000)),
            Pair(700, weight + (daysBetween * 700 / 7000)),
        )
        
        return Uni.createFrom().item(WizardCustomization(null, targetWeight = targetWeightPerRate))
    }

    fun calculateForTargetWeight(age: Int, height: Int, weight: Int, targetWeight: Int): Uni<WizardCustomization> {
        val today = LocalDate.now()

        val difference = (if (targetWeight > weight) {
            targetWeight - weight
        } else {
            weight - targetWeight
        }).toDouble()

        val durationDaysPerRate: Map<Int, LocalDate> = mapOf(
            Pair(100, today.plusDays(Math.round(difference * 7000 / 100))),
            Pair(200, today.plusDays(Math.round(difference * 7000 / 200))),
            Pair(300, today.plusDays(Math.round(difference * 7000 / 300))),
            Pair(400, today.plusDays(Math.round(difference * 7000 / 400))),
            Pair(500, today.plusDays(Math.round(difference * 7000 / 500))),
            Pair(600, today.plusDays(Math.round(difference * 7000 / 600))),
            Pair(700, today.plusDays(Math.round(difference * 7000 / 700))),
        )

        return Uni.createFrom().item(WizardCustomization(durationDaysPerRate, null))
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
    val calculationGoal: CalculationGoal?,
    var bmr: Float = 0f,
    var tdee: Number = 0,
    var deficit: Float = 0f,
    var target: Float = 0f,
    var bmi: Float = 0f,
    var bmiCategory: BmiCategory = BmiCategory.STANDARD_WEIGHT,
    var recommendation: WizardRecommendation = WizardRecommendation.HOLD,
    var targetBmi: List<Int> = listOf(),
    var targetWeight: Float = 0f,
    var targetWeightUpper: Float = 0f,
    var targetWeightLower: Float = 0f,
    var durationDays: Number = 0,
    var durationDaysUpper: Number = 0,
    var durationDaysLower: Number = 0
)

data class WizardCustomization (
    var targetDate: Map<Int, LocalDate>?,
    var targetWeight: Map<Int, Float>?
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

enum class WizardRecommendation {
    HOLD,
    LOSE,
    GAIN
}