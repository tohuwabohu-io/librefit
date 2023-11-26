import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { getDateAsStr } from '$lib/util.js';
import { Category } from '$lib/api/model.js';
import * as dateUtil from 'date-fns';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	/** @type String | undefined */
	const jwt = cookies.get('auth');

	const today = new Date();

	const ctTodayApi = api.listCalorieTrackerEntriesForDate;
	const lastCtResponse = await proxyFetch(fetch, ctTodayApi, jwt, { date: getDateAsStr(today) });

	const weightApi = api.listWeightTrackerEntriesRange;
	const listWeightResponse = await proxyFetch(fetch, weightApi, jwt, {
		dateFrom: getDateAsStr(dateUtil.subMonths(today, 1)),
		dateTo: getDateAsStr(today)
	});

	const ctRangeApi = api.listCalorieTrackerEntriesRange;
	const listCtResponse = await proxyFetch(fetch, ctRangeApi, jwt, {
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

	return {
		authenticated: true,
		lastCt: ctList,
		listWeight: listWeightResponse.ok ? await listWeightResponse.json() : undefined,
		listCt: listCtResponse ? listCtResponse.json() : undefined
	};
};
