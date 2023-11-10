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
export const Category = {
	Breakfast: 'BREAKFAST',
	Lunch: 'LUNCH',
	Dinner: 'DINNER',
	Snack: 'SNACK',
	Unset: 'UNSET'
}

/**
 * @typedef {Object} LocalDate
 */

/**
 * @typedef {Object} LocalDateTime
 */

/**
 * @typedef {Object} UUID
 */

/**
 * @typedef {Object} AuthenticationResponse
 * @property {String} token
 */

/**
 * @typedef {Object} CalorieTrackerEntry
 * @property {String} [userId]
 * @property {LocalDate} added
 * @property {Number} [id]
 * @property {Number} amount
 * @property {Category} category
 * @property {String} [updated]
 * @property {String} [description]
 */

/**
 * @typedef {Object} ErrorResponse
 * @property {Array<String>} messages
 */

/**
 * @typedef {Object} Goal
 * @property {String} [userId]
 * @property {LocalDate} added
 * @property {Number} [id]
 * @property {Number} startAmount
 * @property {Number} endAmount
 * @property {LocalDate} startDate
 * @property {LocalDate} endDate
 * @property {String} [updated]
 */

/**
 * @typedef {Object} LibreUser
 * @property {String} [id]
 * @property {String} email
 * @property {String} password
 * @property {String} role
 * @property {String} [name]
 * @property {String} [registered]
 * @property {String} [lastLogin]
 * @property {String} [avatar]
 */

/**
 * @typedef {Object} Tdee
 * @property {Number} age
 * @property {CalculationSex} sex
 * @property {Number} weight
 * @property {Number} height
 * @property {Number} activityLevel
 * @property {Number} weeklyDifference
 * @property {CalculationGoal} calculationGoal
 * @property {Number} [bmr]
 * @property {Number} [tdee]
 * @property {Number} [deficit]
 * @property {Number} [target]
 */

/**
 * @typedef {Object} WeightTrackerEntry
 * @property {String} [userId]
 * @property {LocalDate} added
 * @property {Number} [id]
 * @property {Number} [amount]
 * @property {String} [updated]
 */

