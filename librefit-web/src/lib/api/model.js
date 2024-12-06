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
