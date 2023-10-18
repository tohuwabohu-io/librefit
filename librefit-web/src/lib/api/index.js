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
 * @typedef {Object} AuthenticationResponse
 * @property {String} token
 */

/**
 * @typedef {Object} CalorieTrackerEntry
 * @property {Number} [userId]
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
 * @property {Number} [userId]
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
 * @property {Number} [id]
 * @property {String} email
 * @property {String} password
 * @property {String} [name]
 * @property {String} [registered]
 * @property {String} [lastLogin]
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
 * @property {Number} [userId]
 * @property {LocalDate} added
 * @property {Number} [id]
 * @property {Number} [amount]
 * @property {String} [updated]
 */

export const api = {
	createGoal: {
		path: '/goals/create',
		/** @see Goal */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteGoal: {
		path: '/goals/delete/{date}/{id}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastGoal: {
		path: '/goals/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readGoal: {
		path: '/goals/read/{date}/{id}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateGoal: {
		path: '/goals/update',
		/** @see Goal */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	calculateTdee: {
		path: '/tdee/calculate/{age}/{sex}/{weight}/{height}/{activityLevel}/{weeklyDifference}/{calculationGoal}',
		contentType: 'text/plain',
		method: 'get',
		guarded: false
	},
	createCalorieTrackerEntry: {
		path: '/tracker/calories/create',
		/** @see CalorieTrackerEntry */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteCalorieTrackerEntry: {
		path: '/tracker/calories/delete/{date}/{id}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	listCalorieTrackerDates: {
		path: '/tracker/calories/list/dates',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listCalorieTrackerEntriesForDate: {
		path: '/tracker/calories/list/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readCalorieTrackerEntry: {
		path: '/tracker/calories/read/{date}/{id}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateCalorieTrackerEntry: {
		path: '/tracker/calories/update',
		/** @see CalorieTrackerEntry */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	createWeightTrackerEntry: {
		path: '/tracker/weight/create',
		/** @see WeightTrackerEntry */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteWeightTrackerEntry: {
		path: '/tracker/weight/delete/{date}/{id}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastWeightTrackerEntry: {
		path: '/tracker/weight/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTrackerEntriesRange: {
		path: '/tracker/weight/list/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTrackerEntries: {
		path: '/tracker/weight/list/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readWeightTrackerEntry: {
		path: '/tracker/weight/read/{date}/{id}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateWeightTrackerEntry: {
		path: '/tracker/weight/update',
		/** @see WeightTrackerEntry */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	postUserLogin: {
		path: '/user/login',
		/** @see LibreUser */
		contentType: 'application/json',
		method: 'post',
		guarded: false
	},
	postUserRegister: {
		path: '/user/register',
		/** @see LibreUser */
		contentType: 'application/json',
		method: 'post',
		guarded: false
	},
}
