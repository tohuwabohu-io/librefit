export const api = {
	getDashboard: {
		path: '/api/composite/dashboard/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	postWizardResult: {
		path: '/api/composite/wizard/result',
		/** @see Wizard */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	postImportBulk: {
		path: '/api/import/bulk',
		contentType: 'multipart/form-data',
		method: 'post',
		guarded: true
	},
	createCalorieTarget: {
		path: '/api/target/calories/create',
		/** @see CalorieTarget */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteCalorieTarget: {
		path: '/api/target/calories/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastCalorieTarget: {
		path: '/api/target/calories/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readCalorieTarget: {
		path: '/api/target/calories/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateCalorieTarget: {
		path: '/api/target/calories/update',
		/** @see CalorieTarget */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	createWeightTarget: {
		path: '/api/target/weight/create',
		/** @see WeightTarget */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteWeightTarget: {
		path: '/api/target/weight/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastWeightTarget: {
		path: '/api/target/weight/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readWeightTarget: {
		path: '/api/target/weight/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateWeightTarget: {
		path: '/api/target/weight/update',
		/** @see WeightTarget */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	listFoodCategories: {
		path: '/api/tracker/calories/categories/list',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	createCalorieTracker: {
		path: '/api/tracker/calories/create',
		/** @see CalorieTracker */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteCalorieTracker: {
		path: '/api/tracker/calories/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	listCalorieTrackerDatesRange: {
		path: '/api/tracker/calories/list/dates/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listCalorieTrackerRange: {
		path: '/api/tracker/calories/list/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listCalorieTrackerForDate: {
		path: '/api/tracker/calories/list/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readCalorieTracker: {
		path: '/api/tracker/calories/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateCalorieTracker: {
		path: '/api/tracker/calories/update',
		/** @see CalorieTracker */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	createWeightTracker: {
		path: '/api/tracker/weight/create',
		/** @see WeightTracker */
		contentType: 'application/json',
		method: 'post',
		guarded: true
	},
	deleteWeightTracker: {
		path: '/api/tracker/weight/delete/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'delete',
		guarded: true
	},
	findLastWeightTracker: {
		path: '/api/tracker/weight/last',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTrackerDatesRange: {
		path: '/api/tracker/weight/list/dates/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTrackerRange: {
		path: '/api/tracker/weight/list/{dateFrom}/{dateTo}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	listWeightTracker: {
		path: '/api/tracker/weight/list/{date}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	readWeightTracker: {
		path: '/api/tracker/weight/read/{date}/{sequence}',
		contentType: 'text/plain',
		method: 'get',
		guarded: true
	},
	updateWeightTracker: {
		path: '/api/tracker/weight/update',
		/** @see WeightTracker */
		contentType: 'application/json',
		method: 'put',
		guarded: true
	},
	postUserLogin: {
		path: '/api/user/login',
		contentType: 'multipart/form-data',
		method: 'post',
		guarded: true
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
	calculateTdee: {
		path: '/api/wizard/calculate/{age}/{sex}/{weight}/{height}/{activityLevel}/{weeklyDifference}/{calculationGoal}',
		contentType: 'text/plain',
		method: 'get',
		guarded: false
	},
	calculateForTargetDate: {
		path: '/api/wizard/custom/date/{age}/{height}/{weight}/{sex}/{targetDate}/{calculationGoal}',
		contentType: 'text/plain',
		method: 'get',
		guarded: false
	},
	calculateForTargetWeight: {
		path: '/api/wizard/custom/weight/{age}/{height}/{weight}/{sex}/{targetWeight}',
		contentType: 'text/plain',
		method: 'get',
		guarded: false
	},
}
