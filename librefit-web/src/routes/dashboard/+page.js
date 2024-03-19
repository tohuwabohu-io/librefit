import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { getDateAsStr } from '$lib/util.js';
import * as dateUtil from 'date-fns';
import { Category } from '$lib/api/model.js';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const userApi = api.readUserInfo;

	const today = new Date();

	// return dashboard relevant data
	const lastWeightApi = api.findLastWeightTrackerEntry;
	const goalApi = api.findLastGoal;
	const ctTodayApi = api.listCalorieTrackerEntriesForDate;
	const weightApi = api.listWeightTrackerEntriesRange;
	const ctRangeApi = api.listCalorieTrackerEntriesRange;

	const user = await proxyFetch(fetch, userApi);
	const lastCtResponse = await proxyFetch(fetch, ctTodayApi, { date: getDateAsStr(today) });

	const listWeightResponse = await proxyFetch(fetch, weightApi, {
		dateFrom: getDateAsStr(dateUtil.subMonths(today, 1)),
		dateTo: getDateAsStr(today)
	});

	const listCtResponse = await proxyFetch(fetch, ctRangeApi, {
		dateFrom: getDateAsStr(dateUtil.subMonths(today, 1)),
		dateTo: getDateAsStr(today)
	});

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

	const lastWeightResponse = await proxyFetch(fetch, lastWeightApi);
	const lastGoalResponse = await proxyFetch(fetch, goalApi);

	if (user.ok) {
		return {
			userData: await user.json(),
			lastWeight: lastWeightResponse.ok ? await lastWeightResponse.json() : undefined,
			currentGoal: lastGoalResponse.ok ? await lastGoalResponse.json() : undefined,
			lastCt: ctList,
			listWeight: listWeightResponse.ok ? await listWeightResponse.json() : undefined,
			listCt: listCtResponse ? listCtResponse.json() : undefined
		};
	}
};
