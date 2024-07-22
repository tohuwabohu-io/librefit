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

    fun validate(wizardInput: WizardInput) {
        val violations = validator.validate(wizardInput)

        if (violations.isNotEmpty()) {
            val errors = violations.map { violation ->
                ErrorDescription(violation.propertyPath.filterNotNull()[0].name, violation.message)
            }

            throw ValidationError(errors)
        }
    }

    fun calculate(wizardInput: WizardInput): Uni<WizardResult> {
        validate(wizardInput)
        
        val wizardResult = WizardResult()

        wizardResult.bmr = calculateBmr(wizardInput.sex, wizardInput.weight.toFloat(), wizardInput.height.toFloat(), wizardInput.age.toFloat())
        wizardResult.tdee = calculateTdee(wizardInput.activityLevel, wizardResult.bmr)
        wizardResult.deficit = calculateDeficit(wizardInput.weeklyDifference.toFloat())
        wizardResult.target = calculateTarget(wizardInput.calculationGoal, wizardResult.tdee, wizardResult.deficit)
        wizardResult.bmi = calculateBmi(wizardInput.weight, wizardInput.height)
        wizardResult.bmiCategory = calculateBmiCategory(wizardInput.sex, wizardResult.bmi)
        wizardResult.targetBmi = calculateTargetBmi(wizardInput.age)
        wizardResult.targetWeight = calculateTargetWeight(wizardResult.targetBmi, wizardInput.height.toFloat())
        wizardResult.targetWeightLower = calculateTargetWeightLower(wizardResult.targetBmi, wizardInput.height.toFloat())
        wizardResult.targetWeightUpper = calculateTargetWeightUpper(wizardResult.targetBmi, wizardInput.height.toFloat())
        wizardResult.recommendation = calculateRecommendation(wizardResult.bmiCategory)

        calculateDurationDays(wizardInput, wizardResult)

        return Uni.createFrom().item(wizardResult)
    }

    internal fun calculateBmr(sex: CalculationSex, weight: Float, height: Float, age: Float): Float = when (sex) {
        CalculationSex.MALE -> round(66 + (13.7 * weight) + (5 * height) - (6.8 * age)).toFloat()
        CalculationSex.FEMALE -> round(655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)).toFloat()
    }

    internal fun calculateTdee(activityLevel: Float, bmr: Float) = round(activityLevel * bmr)

    internal fun calculateDeficit(weeklyDifference: Float) = weeklyDifference / 10 * 7000 / 7

    internal fun calculateTarget(calculationGoal: CalculationGoal?, tdee: Number, deficit: Float): Float = when (calculationGoal) {
        CalculationGoal.GAIN -> tdee.toFloat() + deficit
        CalculationGoal.LOSS -> tdee.toFloat() - deficit;
        null -> tdee as Float
    }

    internal fun calculateBmi(weight: Number, height: Number) = round(weight.toFloat() / ((height.toFloat() / 100).pow(2)))

    internal fun calculateBmiCategory(sex: CalculationSex, bmi: Float): BmiCategory = when (sex) {
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

    internal fun calculateTargetBmi(age: Number): List<Int> = when (age) {
        in 19..24 -> listOf(19, 24)
        in 25..34 -> listOf(20, 25)
        in 35..44 -> listOf(21, 26)
        in 45..54 -> listOf(22, 27)
        in 55..64 -> listOf(23, 28)
        else -> listOf(24, 29)
    }

    internal fun calculateTargetWeight(targetBmi: List<Int>, height: Float) =
        round(((targetBmi[0] + targetBmi[1]).toFloat() / 2) * (height / 100).pow(2))

    internal fun calculateTargetWeightLower(targetBmi: List<Int>, height: Float) =
        round(((targetBmi[0]).toFloat()) * (height / 100).pow(2))

    internal fun calculateTargetWeightUpper(targetBmi: List<Int>, height: Float) =
        round(((targetBmi[1]).toFloat()) * (height / 100).pow(2))

    internal fun calculateDurationDays(wizardInput: WizardInput, wizardResult: WizardResult) {
        when (wizardInput.calculationGoal) {
            CalculationGoal.GAIN -> {
                wizardResult.durationDays = (wizardResult.targetWeight - wizardInput.weight.toFloat()) * 7000 / wizardResult.deficit
                wizardResult.durationDaysUpper = (wizardResult.targetWeightUpper - wizardInput.weight.toFloat()) * 7000 / wizardResult.deficit
                wizardResult.durationDaysLower = (wizardResult.targetWeightLower - wizardInput.weight.toFloat()) * 7000 / wizardResult.deficit
            }

            CalculationGoal.LOSS -> {
                wizardResult.durationDays = (wizardInput.weight.toFloat() - wizardResult.targetWeight) * 7000 / wizardResult.deficit
                wizardResult.durationDaysUpper = (wizardInput.weight.toFloat() - wizardResult.targetWeightUpper) * 7000 / wizardResult.deficit
                wizardResult.durationDaysLower = (wizardInput.weight.toFloat() - wizardResult.targetWeightLower) * 7000 / wizardResult.deficit
            }

            null -> {
                wizardResult.durationDays = 0
                wizardResult.durationDaysLower = 0
                wizardResult.durationDaysUpper = 0
            }
        }
    }

    internal fun calculateRecommendation(bmiCategory: BmiCategory): WizardRecommendation = when (bmiCategory) {
        BmiCategory.STANDARD_WEIGHT -> WizardRecommendation.HOLD
        BmiCategory.UNDERWEIGHT -> WizardRecommendation.GAIN
        BmiCategory.OVERWEIGHT -> WizardRecommendation.LOSE
        BmiCategory.OBESE -> WizardRecommendation.LOSE
        BmiCategory.SEVERELY_OBESE -> WizardRecommendation.LOSE
    }

    fun calculateForTargetDate(age: Int, height: Int, currentWeight: Float, sex: CalculationSex, targetDate: LocalDate): Uni<WizardTargetDate> {
        val today = LocalDate.now()

        val daysBetween = ChronoUnit.DAYS.between(today, targetDate)

        val currentBmi = calculateBmi(currentWeight, height)
        val currentClassification = calculateBmiCategory(sex, currentBmi);

        val warning = false
        val message = ""

        val rates: List<Int> = listOf(100, 200, 300, 400, 500, 600, 700)
        val targetWeightPerRate: Map<Int, Float> = rates.associateWith { currentWeight + (daysBetween * it) / 700 }

        return Uni.createFrom().item(WizardTargetDate(targetWeightPerRate, warning, message))
    }

    fun calculateForTargetWeight(startDate: LocalDate, age: Int, height: Int, currentWeight: Int, sex: CalculationSex, targetWeight: Int): Uni<WizardTargetWeight> {
        val currentBmi = calculateBmi(currentWeight, height)
        val targetWeightBmi = calculateBmi(targetWeight, height)

        val currentClassification = calculateBmiCategory(sex, currentBmi);
        val targetClassification = calculateBmiCategory(sex, targetWeightBmi)

        val difference = (if (targetWeight > currentWeight) {
            targetWeight - currentWeight
        } else {
            currentWeight - targetWeight
        }).toDouble()

        val warning = targetClassification == BmiCategory.UNDERWEIGHT || targetClassification == BmiCategory.OBESE || targetClassification == BmiCategory.SEVERELY_OBESE
        var message = ""

        if (targetClassification == BmiCategory.UNDERWEIGHT) {
            if (targetWeight < currentWeight) {
                message = if (currentClassification == BmiCategory.UNDERWEIGHT) {
                    "Your target weight is even lower that your current weight. Considering that you are currently underweight, I cannot recommend you to proceed."
                } else {
                    "Your target weight will classify you as underweight. You should revisit your choice."
                }
            }
        } else if (targetClassification == BmiCategory.OBESE) {
            if (targetWeight > currentWeight) {
                message = if (currentClassification == BmiCategory.OBESE) {
                    "Your target weight is even higher that your current weight. Considering that you are currently obese, I cannot recommend you to proceed."
                } else {
                    "Your target weight will classify you as obese. You should revisit your choice."
                }
            }
        } else if (targetClassification == BmiCategory.SEVERELY_OBESE) {
            if (targetWeight > currentWeight) {
                message = if(currentClassification == BmiCategory.SEVERELY_OBESE) {
                    "Your target weight is even higher that your current weight. Considering that you are currently severely obese, I cannot recommend you to proceed."
                } else {
                    "Your target weight will classify you as severely obese. You should revisit your choice."
                }
            }
        }

        val rates: List<Int> = listOf(100, 200, 300, 400, 500, 600, 700)
        val datePerRate: Map<Int, LocalDate> = rates.associateWith { startDate.plusDays(Math.round(difference * 7000 / it)) }

        return Uni.createFrom().item(WizardTargetWeight(datePerRate, targetClassification, warning, message))
    }
}

/**
 * Calculation of basic metabolic rate (BMR) and total daily energy expenditure (TDEE) based on the Harris-Benedict
 * formula.
 */
data class WizardInput(
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
)

data class WizardResult (
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

data class WizardTargetWeight (
    var datePerRate: Map<Int, LocalDate>,
    var targetClassification: BmiCategory,
    val warning: Boolean,
    val message: String
)

data class WizardTargetDate (
    var targetWeightPerRate: Map<Int, Float>,
    val warning: Boolean,
    val message: String
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