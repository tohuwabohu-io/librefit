import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { getDateAsStr } from '$lib/util.js';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	const loadCtDateApi = api.listCalorieTrackerDates;
	const listCtForDateApi = api.listCalorieTrackerEntriesForDate;

	const jwt = cookies.get('auth');

	const ctDateResponse = await proxyFetch(fetch, loadCtDateApi, jwt);
	const listCtForDateResponse = await proxyFetch(fetch, listCtForDateApi, jwt, {
		date: getDateAsStr(new Date()) // today
	});

	if (
		ctDateResponse.status === 200 &&
		(listCtForDateResponse.status === 200 || listCtForDateResponse.status === 404) // 'not found' means no entry created today
	) {
		const todayStr = getDateAsStr(new Date());

		/** @type {Array<String>} */
		const availableDates = await ctDateResponse.json();

		if (availableDates.indexOf(todayStr) < 0) {
			availableDates.unshift(todayStr);
		}

		return {
			availableDates: availableDates,
			entryToday:
				listCtForDateResponse.status === 200 ? await listCtForDateResponse.json() : undefined
		};
	} else {
		return { error: 'An error has occured. Please try again later.' };
	}
};
