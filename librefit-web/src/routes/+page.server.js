import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { getDateAsStr } from '$lib/util.js';
import { Category } from '$lib/api/model.js';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	/** @type String | undefined */
	const jwt = cookies.get('auth');

	// show default main page
	if (!jwt || jwt === 'undefined') {
		return {
			authenticated: false
		};
	}

	const today = new Date();

	// return dashboard relevant data
	const lastWeightApi = api.findLastWeightTrackerEntry;
	const goalApi = api.findLastGoal;
	const ctTodayApi = api.listCalorieTrackerEntriesForDate;
	const userApi = api.readUserInfo;

	const lastWeightResponse = await proxyFetch(fetch, lastWeightApi, jwt);
	const lastGoalResponse = await proxyFetch(fetch, goalApi, jwt);
	const lastCtResponse = await proxyFetch(fetch, ctTodayApi, jwt, { date: getDateAsStr(today) });

	/** @type {Array<CalorieTrackerEntry>} */
	const ctList = await lastCtResponse.json();

	// add a blank entry for new input
	/** @type {CalorieTrackerEntry} */
	const blankEntry = {
		added: getDateAsStr(today),
		amount: 0,
		category: Category.Unset
	};

	ctList.unshift(blankEntry);

	const userData = await proxyFetch(fetch, userApi, jwt);

	return {
		authenticated: true,
		lastWeight: await lastWeightResponse.json(),
		lastGoal: await lastGoalResponse.json(),
		lastCt: ctList,
		user: await userData.json()
	};
};
