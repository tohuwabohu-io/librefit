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

	const lastWeightResponse = await proxyFetch(fetch, lastWeightApi, jwt);
	const lastGoalResponse = await proxyFetch(fetch, goalApi, jwt);
	const lastCtResponse = await proxyFetch(fetch, ctTodayApi, jwt, { date: getDateAsStr(today) });

	/** @type Array<CalorieTrackerEntry> */
	const ctList = [];

	if (lastCtResponse.ok) {
		ctList.push(...(await lastCtResponse.json()));

		// add a blank entry for new input
		/** @type {CalorieTrackerEntry} */
		const blankEntry = {
			added: getDateAsStr(today),
			amount: 0,
			category: Category.Unset
		};

		ctList.unshift(blankEntry);
	}

	return {
		authenticated: true,
		lastWeight: lastWeightResponse.ok ? await lastWeightResponse.json() : undefined,
		lastGoal: lastGoalResponse.ok ? await lastGoalResponse.json() : undefined,
		lastCt: ctList.length > 0 ? ctList : lastCtResponse
	};
};
