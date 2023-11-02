import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { getDateAsStr } from '$lib/util.js';

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

	const lastWeightResponse = await proxyFetch(fetch, lastWeightApi, jwt);
	const lastGoalResponse = await proxyFetch(fetch, goalApi, jwt);
	const lastCtResponse = await proxyFetch(fetch, ctTodayApi, jwt, { date: getDateAsStr(today) });

	return {
		authenticated: true,
		lastWeight: await lastWeightResponse.json(),
		lastGoal: await lastGoalResponse.json(),
		lastCt: await lastCtResponse.json()
	};
};
