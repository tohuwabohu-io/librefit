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
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.math.round

@ApplicationScoped
class Wizard {
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

    fun validate(wizardTargetWeightInput: WizardTargetWeightInput) {
        val violations = validator.validate(wizardTargetWeightInput)

        if (violations.isNotEmpty()) {
            val errors = violations.map { violation ->
                ErrorDescription(violation.propertyPath.filterNotNull()[0].name, violation.message)
            }

            throw ValidationError(errors)
        }
    }

    fun validate(wizardTargetDateInput: WizardTargetDateInput) {
        val violations = validator.validate(wizardTargetDateInput)

        if (violations.isNotEmpty()) {
            val errors = violations.map { violation ->
                ErrorDescription(violation.propertyPath.filterNotNull()[0].name, violation.message)
            }

            throw ValidationError(errors)
        }
    }

    /**
     * Calculation of basic metabolic rate (BMR) and total daily energy expenditure (TDEE) based on the Harris-Benedict
     * formula.
     */
    fun calculate(wizardInput: WizardInput): Uni<WizardResult> {
        validate(wizardInput)
        
        val wizardResult = WizardResult()

        val targetBmiRange = calculateTargetBmi(wizardInput.age)

        wizardResult.bmr = calculateBmr(wizardInput.sex, wizardInput.weight.toFloat(), wizardInput.height.toFloat(), wizardInput.age.toFloat())
        wizardResult.tdee = calculateTdee(wizardInput.activityLevel, wizardResult.bmr)
        wizardResult.deficit = calculateDeficit(wizardInput.weeklyDifference.toFloat())
        wizardResult.target = calculateTarget(wizardInput.calculationGoal, wizardResult.tdee, wizardResult.deficit)
        wizardResult.bmi = calculateBmi(wizardInput.weight, wizardInput.height)
        wizardResult.bmiCategory = calculateBmiCategory(wizardInput.sex, wizardResult.bmi)
        wizardResult.targetBmi = (targetBmiRange.first + targetBmiRange.last) / 2
        wizardResult.targetBmiLower = targetBmiRange.first
        wizardResult.targetBmiUpper = targetBmiRange.last
        wizardResult.targetWeight = calculateTargetWeight(targetBmiRange, wizardInput.height.toFloat())
        wizardResult.targetWeightLower = calculateTargetWeightLower(targetBmiRange, wizardInput.height.toFloat())
        wizardResult.targetWeightUpper = calculateTargetWeightUpper(targetBmiRange, wizardInput.height.toFloat())
        wizardResult.recommendation = calculateRecommendation(wizardResult.bmiCategory)

        calculateDurationDaysTotal(wizardInput, wizardResult)

        return Uni.createFrom().item(wizardResult)
    }

    internal fun calculateBmr(sex: CalculationSex, weight: Float, height: Float, age: Float): Float = when (sex) {
        CalculationSex.MALE -> round(66 + (13.7 * weight) + (5 * height) - (6.8 * age)).toFloat()
        CalculationSex.FEMALE -> round(655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)).toFloat()
    }

    internal fun calculateTdee(activityLevel: Float, bmr: Float) = round(activityLevel * bmr)

    internal fun calculateDeficit(weeklyDifference: Float) = weeklyDifference / 10 * 7000 / 7

    internal fun calculateTarget(calculationGoal: CalculationGoal, tdee: Number, deficit: Float): Float = when (calculationGoal) {
        CalculationGoal.GAIN -> tdee.toFloat() + deficit
        CalculationGoal.LOSS -> tdee.toFloat() - deficit;
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

    internal fun calculateTargetBmi(age: Number): IntRange = when (age) {
        in 19..24 -> 19..24
        in 25..34 -> 20..25
        in 35..44 -> 21..26
        in 45..54 -> 22..27
        in 55..64 -> 23..28
        else -> 24..29
    }

    internal fun calculateTargetWeight(targetBmi: IntRange, height: Float) =
        round(((targetBmi.first + targetBmi.last).toFloat() / 2) * (height / 100).pow(2))

    internal fun calculateTargetWeightLower(targetBmi: IntRange, height: Float) =
        round(((targetBmi.first).toFloat()) * (height / 100).pow(2))

    internal fun calculateTargetWeightUpper(targetBmi: IntRange, height: Float) =
        round(((targetBmi.last).toFloat()) * (height / 100).pow(2))

    internal fun calculateDurationDaysTotal(wizardInput: WizardInput, wizardResult: WizardResult) {
        when (wizardInput.calculationGoal) {
            CalculationGoal.GAIN -> {
                wizardResult.durationDays = calculateDurationDays(wizardResult.targetWeight, wizardInput.weight.toFloat(), wizardResult.deficit)
                wizardResult.durationDaysUpper = calculateDurationDays(wizardResult.targetWeightUpper, wizardInput.weight.toFloat(), wizardResult.deficit)
                wizardResult.durationDaysLower = calculateDurationDays(wizardResult.targetWeightLower, wizardInput.weight.toFloat(), wizardResult.deficit)
            }

            CalculationGoal.LOSS -> {
                wizardResult.durationDays = calculateDurationDays(wizardInput.weight.toFloat(), wizardResult.targetWeight, wizardResult.deficit)
                wizardResult.durationDaysUpper = calculateDurationDays(wizardInput.weight.toFloat(), wizardResult.targetWeightUpper, wizardResult.deficit)
                wizardResult.durationDaysLower = calculateDurationDays(wizardInput.weight.toFloat(), wizardResult.targetWeightLower, wizardResult.deficit)
            }
        }
    }

    internal fun calculateDurationDays(weight: Float, targetWeight: Float, deficit: Float) = ((weight - targetWeight) * 7000) / deficit

    internal fun calculateRecommendation(bmiCategory: BmiCategory): WizardRecommendation = when (bmiCategory) {
        BmiCategory.STANDARD_WEIGHT -> WizardRecommendation.HOLD
        BmiCategory.UNDERWEIGHT -> WizardRecommendation.GAIN
        BmiCategory.OVERWEIGHT -> WizardRecommendation.LOSE
        BmiCategory.OBESE -> WizardRecommendation.LOSE
        BmiCategory.SEVERELY_OBESE -> WizardRecommendation.LOSE
    }

    /**
     * Calculation of realistic weight loss/gain goals until a specific date occurs.
     */
    fun calculateForTargetDate(input: WizardTargetDateInput): Uni<WizardTargetDateResult> {
        validate(input)

        val today = LocalDate.now()

        val daysBetween = ChronoUnit.DAYS.between(today, input.targetDate)

        val currentBmi = calculateBmi(input.currentWeight, input.height)
        val currentClassification = calculateBmiCategory(input.sex, currentBmi)

        val rates: List<Int> = listOf(100, 200, 300, 400, 500, 600, 700)
        val multiplier = if (input.calculationGoal === CalculationGoal.LOSS) -1 else 1
        val obeseList = listOf(BmiCategory.OBESE, BmiCategory.SEVERELY_OBESE)

        var resultByRate: Map<Int, WizardResult> = rates.associateWith {
            val weight = (input.currentWeight.toFloat() + Math.round((multiplier * (daysBetween * it) / 7000).toDouble()))

            val result = WizardResult()
            result.bmi = calculateBmi(weight, input.height)
            result.bmiCategory = calculateBmiCategory(input.sex, result.bmi)
            result.targetWeight = weight

            result
        }.filter { (_, result) ->
            /**
             * filter non-recommendable results:
             * - weight loss should not lead to an underweight BMI
             * - weight gain should not lead to an obese or severally obese BMI
             * - no weight gain option current BMI is of obese or severally obese category
             */
            when (input.calculationGoal) {
                CalculationGoal.LOSS -> {
                    result.bmiCategory != BmiCategory.UNDERWEIGHT
                }
                else -> {
                    !obeseList.contains(result.bmiCategory) || obeseList.contains(currentClassification)
                }
            }
        }

        // calculate a custom rate to present at least one viable result
        if (resultByRate.isEmpty()) {
            val targetBmi = calculateTargetBmi(input.age)
            val targetWeight = calculateTargetWeight(targetBmi, input.height.toFloat())

            val difference = when (input.calculationGoal) {
                CalculationGoal.GAIN -> {
                    Math.round(targetWeight - input.currentWeight.toFloat())
                }
                CalculationGoal.LOSS -> {
                    Math.round(input.currentWeight.toFloat() - targetWeight)
                }
            }

            val rate = Math.round(difference.toFloat() * 7000 / daysBetween)

            val result = WizardResult()
            result.bmi = calculateBmi(targetWeight, input.height)
            result.bmiCategory = calculateBmiCategory(input.sex, result.bmi)
            result.targetWeight = targetWeight

            resultByRate = mapOf(rate to result)
        }

        return Uni.createFrom().item(WizardTargetDateResult(
            resultByRate
        ))
    }

    /**
     * Calculation of length until a specific weight can be obtained.
     */
    fun calculateForTargetWeight(input: WizardTargetWeightInput): Uni<WizardTargetWeightResult> {
        validate(input)

        val currentBmi = calculateBmi(input.currentWeight, input.height)
        val targetWeightBmi = calculateBmi(input.targetWeight, input.height)

        val currentClassification = calculateBmiCategory(input.sex, currentBmi);
        val targetClassification = calculateBmiCategory(input.sex, targetWeightBmi)

        val difference = (if (input.targetWeight > input.currentWeight) {
            input.targetWeight - input.currentWeight
        } else {
            input.currentWeight - input.targetWeight
        }).toDouble()

        val warning = targetClassification == BmiCategory.UNDERWEIGHT || targetClassification == BmiCategory.OBESE || targetClassification == BmiCategory.SEVERELY_OBESE
        var message = ""

        if (targetClassification == BmiCategory.UNDERWEIGHT) {
            if (input.targetWeight < input.currentWeight) {
                message = if (currentClassification == BmiCategory.UNDERWEIGHT) {
                    "Your target weight is even lower that your current weight. Considering that you are currently underweight, I cannot recommend you to proceed."
                } else {
                    "Your target weight will classify you as underweight. You should revisit your choice."
                }
            }
        } else if (targetClassification == BmiCategory.OBESE) {
            if (input.targetWeight > input.currentWeight) {
                message = if (currentClassification == BmiCategory.OBESE) {
                    "Your target weight is even higher that your current weight. Considering that you are currently obese, I cannot recommend you to proceed."
                } else {
                    "Your target weight will classify you as obese. You should revisit your choice."
                }
            }
        } else if (targetClassification == BmiCategory.SEVERELY_OBESE) {
            if (input.targetWeight > input.currentWeight) {
                message = if(currentClassification == BmiCategory.SEVERELY_OBESE) {
                    "Your target weight is even higher that your current weight. Considering that you are currently severely obese, I cannot recommend you to proceed."
                } else {
                    "Your target weight will classify you as severely obese. You should revisit your choice."
                }
            }
        }

        var datePerRate: Map<Int, LocalDate> = emptyMap()

        if ("" == message) {
            val rates: List<Int> = listOf(100, 200, 300, 400, 500, 600, 700)
            datePerRate = rates.associateWith { input.startDate.plusDays(Math.round(difference * 7000 / it)) }
        }

        return Uni.createFrom().item(WizardTargetWeightResult(datePerRate, targetClassification, warning, message))
    }
}

data class WizardInput (
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
)

data class WizardResult (
    var bmr: Float = 0f,
    var tdee: Number = 0,
    var deficit: Float = 0f,
    var target: Float = 0f,
    var bmi: Float = 0f,
    var bmiCategory: BmiCategory = BmiCategory.STANDARD_WEIGHT,
    var recommendation: WizardRecommendation = WizardRecommendation.HOLD,
    var targetBmi: Int = 23,
    var targetBmiUpper: Int = 24,
    var targetBmiLower: Int = 20,
    var targetWeight: Float = 0f,
    var targetWeightUpper: Float = 0f,
    var targetWeightLower: Float = 0f,
    var durationDays: Number = 0,
    var durationDaysUpper: Number = 0,
    var durationDaysLower: Number = 0
)

data class WizardTargetWeightInput (
    @field:Min(value = 18, message = "Your age must be between 18 and 99 years.")
    @field:Max(value = 99, message = "Your age must be between 18 and 99 years.")
    val age: Number,

    val sex: CalculationSex,

    @field:Min(value = 30, message = "Please provide a weight between 30kg and 300kg.")
    @field:Max(value = 300, message = "Please provide a weight between 30kg and 300kg.")
    val currentWeight: Float,

    @field:Min(value = 100, message = "Please provide a height between 100cm and 220cm.")
    @field:Max(value = 300, message = "Please provide a height between 100cm and 220cm.")
    val height: Number,

    @field:Min(value = 30, message = "Please provide a weight between 30kg and 300kg.")
    @field:Max(value = 300, message = "Please provide a weight between 30kg and 300kg.")
    val targetWeight: Float,

    val startDate: LocalDate
)

data class WizardTargetWeightResult (
    var datePerRate: Map<Int, LocalDate> = emptyMap(),
    var targetClassification: BmiCategory = BmiCategory.STANDARD_WEIGHT,
    val warning: Boolean,
    val message: String
)

data class WizardTargetDateInput (
    @field:Min(value = 18, message = "Your age must be between 18 and 99 years.")
    @field:Max(value = 99, message = "Your age must be between 18 and 99 years.")
    val age: Number,

    val sex: CalculationSex,

    @field:Min(value = 30, message = "Please provide a weight between 30kg and 300kg.")
    @field:Max(value = 300, message = "Please provide a weight between 30kg and 300kg.")
    val currentWeight: Number,

    @field:Min(value = 100, message = "Please provide a height between 100cm and 220cm.")
    @field:Max(value = 300, message = "Please provide a height between 100cm and 220cm.")
    val height: Number,

    val calculationGoal: CalculationGoal,

    @field:NotNull
    val targetDate: LocalDate
)


data class WizardTargetDateResult (
    var resultByRate: Map<Int, WizardResult> = emptyMap(),
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