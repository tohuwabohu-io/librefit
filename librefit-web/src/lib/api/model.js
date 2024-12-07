/**
 * @readonly
 * @enum {String}
 */
export const BmiCategory = {
	Underweight: 'UNDERWEIGHT',
	Standard_weight: 'STANDARD_WEIGHT',
	Overweight: 'OVERWEIGHT',
	Obese: 'OBESE',
	Severely_obese: 'SEVERELY_OBESE'
}

/**
 * @readonly
 * @enum {String}
 */
export const CalculationGoal = {
	Gain: 'GAIN',
	Loss: 'LOSS'
}

/**
 * @readonly
 * @enum {String}
 */
export const CalculationSex = {
	Male: 'MALE',
	Female: 'FEMALE'
}

/**
 * @readonly
 * @enum {String}
 */
export const WizardRecommendation = {
	Hold: 'HOLD',
	Lose: 'LOSE',
	Gain: 'GAIN'
}

/**
 * @typedef {Object} Dashboard
 * @property {LibreUser} userData - The user data associated with the dashboard.
 * @property {CalorieTarget|null} [calorieTarget] - The optional calorie target for the user.
 * @property {CalorieTracker[]} caloriesTodayList - A list of calorie tracker entries for today.
 * @property {CalorieTracker[]} caloriesWeekList - A list of calorie tracker entries for the week.
 * @property {WeightTarget|null} [weightTarget] - The optional weight target for the user.
 * @property {WeightTracker[]} weightTodayList - A list of weight tracker entries for today.
 * @property {WeightTracker[]} weightMonthList - A list of weight tracker entries for the month.
 * @property {FoodCategory[]} foodCategories - A list of food categories.
 */

/**
 * @typedef {Object} LibreUser
 * @property {number} id - The unique identifier for the user.
 * @property {string|null} [avatar] - The optional avatar URL of the user.
 * @property {string|null} [name] - The optional name of the user.
 */

/**
 * @typedef {Object} CalorieTracker
 * @property {number} id - The unique identifier of the calorie tracker entry.
 * @property {string} added - The date the entry was added.
 * @property {number} amount - The amount of calories recorded.
 * @property {string} category - The category of calories.
 * @property {string|null} [description] - An optional description of the entry.
 */

/**
 * @typedef {Object} WeightTracker
 * @property {number} id - The unique identifier of the weight tracker entry.
 * @property {string} added - The date the entry was added.
 * @property {number} amount - The weight recorded.
 */

/**
 * @typedef {Object} FoodCategory
 * @property {string}  - The full name of the food category.
 * @property {string} shortvalue - The abbreviated name of the food category.
 */

/**
 * @typedef {Object} CalorieTarget
 * @property {number} id - The unique identifier of the calorie target.
 * @property {string} added - The date the target was added.
 * @property {string} endDate - The end date for the calorie target.
 * @property {number} maximumCalories - The maximum calorie limit.
 * @property {string} startDate - The start date for the calorie target.
 * @property {number} targetCalories - The target calorie intake.
 */

/**
 * @typedef {Object} WeightTarget
 * @property {number} id - The unique identifier of the weight target.
 * @property {string} added - The date the target was added.
 * @property {string} endDate - The end date for the weight target.
 * @property {number} initialWeight - The initial weight at the start of the target period.
 * @property {string} startDate - The start date for the weight target.
 * @property {number} targetWeight - The target weight.
 */

/**
 * @typedef {Object} NewCalorieTracker
 * @property {string} added - The date the entry is added.
 * @property {number} amount - The amount of calories to be recorded.
 * @property {string} category - The category of calories.
 * @property {string} description - A description of the entry.
 */

/**
 * @typedef {Object} NewWeightTracker
 * @property {string} added - The date the entry is added.
 * @property {number} amount - The weight to record.
 */

/**
 * @typedef {Object} NewFoodCategory
 * @property {string} longvalue - The full name of the food category.
 * @property {string} shortvalue - The abbreviated name of the food category.
 */

/**
 * @typedef {Object} NewCalorieTarget
 * @property {string} added - The date the target is added.
 * @property {string} endDate - The end date for the calorie target.
 * @property {number} maximumCalories - The maximum calorie limit.
 * @property {string} startDate - The start date for the calorie target.
 * @property {number} targetCalories - The target calorie intake.
 */

/**
 * @typedef {Object} NewWeightTarget
 * @property {string} added - The date the target is added.
 * @property {string} endDate - The end date for the weight target.
 * @property {number} initialWeight - The initial weight at the start of the target period.
 * @property {string} startDate - The start date for the weight target.
 * @property {number} targetWeight - The target weight.
 */

/**
 * @typedef {Object} Wizard
 * @property {CalorieTarget} calorieTarget - The calorie target.
 * @property {WeightTarget} weightTarget - The weight target.
 * @property {WeightTracker} weightTracker - The weight tracker.
 */

/**
 * @typedef {Object} WizardInput
 * @property {number} age - The age of the user (must be between 18 and 99).
 * @property {string} sex - The sex of the user (CalculationSex).
 * @property {number} weight - The weight of the user (must be between 30 and 300).
 * @property {number} height - The height of the user (must be between 100 and 300).
 * @property {number} activityLevel - The activity level of the user, validated with a custom function.
 * @property {number} weeklyDifference - The weekly difference (must be between 0 and 7).
 * @property {string} calculationGoal - The calculation goal (CalculationGoal).
 */

/**
 * @typedef {Object} WizardResult
 * @property {number} bmr - Basal Metabolic Rate.
 * @property {number} tdee - Total Daily Energy Expenditure.
 * @property {number} deficit - Calorie deficit.
 * @property {number} target - Target calories.
 * @property {number} bmi - Body Mass Index.
 * @property {string} bmiCategory - BMI category (BmiCategory).
 * @property {string} recommendation - Recommendation (WizardRecommendation).
 * @property {number} targetBmi - Target BMI.
 * @property {number} targetBmiUpper - Upper target BMI.
 * @property {number} targetBmiLower - Lower target BMI.
 * @property {number} targetWeight - Target weight.
 * @property {number} targetWeightUpper - Upper target weight.
 * @property {number} targetWeightLower - Lower target weight.
 * @property {number} durationDays - Duration in days.
 * @property {number} durationDaysUpper - Upper duration in days.
 * @property {number} durationDaysLower - Lower duration in days.
 */

/**
 * @typedef {Object} WizardTargetWeightInput
 * @property {number} age - The age of the user.
 * @property {string} sex - The sex of the user (CalculationSex).
 * @property {number} currentWeight - The current weight.
 * @property {number} height - The height of the user.
 * @property {number} targetWeight - The target weight.
 * @property {string} startDate - The start date (deserialized with custom date function).
 */

/**
 * @typedef {Object} WizardTargetWeightResult
 * @property {Object} datePerRate - Date per rate map.
 * @property {string} targetClassification - Target classification (BmiCategory).
 * @property {boolean} warning - Indicates if there's a warning.
 * @property {string} message - Message to the user.
 */

/**
 * @typedef {Object} WizardTargetDateInput
 * @property {number} age - The age of the user.
 * @property {string} sex - The sex of the user (CalculationSex).
 * @property {number} currentWeight - The current weight.
 * @property {number} height - The height of the user.
 * @property {string} calculationGoal - Calculation goal (CalculationGoal).
 * @property {string} targetDate - The target date (deserialized with custom date function).
 */
