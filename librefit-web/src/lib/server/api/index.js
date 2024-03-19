export const api = {
	createGoal: {
		path: '/api/goals/create',
		/** @see Goal */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteGoal: {
		path: '/api/goals/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastGoal: {
		path: '/api/goals/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readGoal: {
		path: '/api/goals/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateGoal: {
		path: '/api/goals/update',
		/** @see Goal */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	postImportBulk: {
		path: '/api/import/bulk',
		contentType: 'multipart/form-data',
		method: 'post',
		guarded: true
	},
	calculateTdee: {
		path: '/api/tdee/calculate/{age}/{sex}/{weight}/{height}/{activityLevel}/{weeklyDifference}/{calculationGoal}',
		contentType: 'text/plain',
		method: 'get',
		guarded: false
	},
	createCalorieTrackerEntry: {
		path: '/api/tracker/calories/create',
		/** @see CalorieTrackerEntry */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteCalorieTrackerEntry: {
		path: '/api/tracker/calories/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	listCalorieTrackerDates: {
		path: '/api/tracker/calories/list/dates',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listCalorieTrackerEntriesRange: {
		path: '/api/tracker/calories/list/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listCalorieTrackerEntriesForDate: {
		path: '/api/tracker/calories/list/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readCalorieTrackerEntry: {
		path: '/api/tracker/calories/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateCalorieTrackerEntry: {
		path: '/api/tracker/calories/update',
		/** @see CalorieTrackerEntry */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	createWeightTrackerEntry: {
		path: '/api/tracker/weight/create',
		/** @see WeightTrackerEntry */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteWeightTrackerEntry: {
		path: '/api/tracker/weight/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastWeightTrackerEntry: {
		path: '/api/tracker/weight/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTrackerEntriesRange: {
		path: '/api/tracker/weight/list/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTrackerEntries: {
		path: '/api/tracker/weight/list/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readWeightTrackerEntry: {
		path: '/api/tracker/weight/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateWeightTrackerEntry: {
		path: '/api/tracker/weight/update',
		/** @see WeightTrackerEntry */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	activateUser: {
		path: '/api/user/activate/{activationId}',
		contentType: 'text/plain',
		method: 'get',
		guarded: false
	},
	postUserLogin: {
		path: '/api/user/login',
		/** @see LibreUser */
		contentType: 'application/json',
		method: 'post',
		guarded: false
	},
	postUserLogout: {
		path: '/api/user/logout',
		contentType: 'text/plain',
		method: 'post',
		guarded: true
	},
	readUserInfo: {
		path: '/api/user/read',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	postUserRefresh: {
		path: '/api/user/refresh',
		/** @see AuthInfo */
		contentType: 'application/json',
		method: 'post',
		guarded: false
	},
	postUserRegister: {
		path: '/api/user/register',
		/** @see LibreUser */
		contentType: 'application/json',
		method: 'post',
		guarded: false
	},
	updateUserInfo: {
		path: '/api/user/update',
		/** @see LibreUser */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
}
