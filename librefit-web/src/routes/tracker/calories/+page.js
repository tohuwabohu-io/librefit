import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { Category } from '$lib/api/model.js';
import { getDateAsStr } from '$lib/date.js';

export const load = async ({ fetch, cookies }) => {
	const loadCtDateApi = api.listCalorieTrackerDates;
	const listCtForDateApi = api.listCalorieTrackerEntriesForDate;

	const ctDateResponse = await proxyFetch(fetch, loadCtDateApi);
	const listCtForDateResponse = await proxyFetch(fetch, listCtForDateApi, {
		date: getDateAsStr(new Date()) // today
	});

	if (ctDateResponse.status === 200 && listCtForDateResponse.status === 200) {
		const todayStr = getDateAsStr(new Date());

		/** @type {Array<String>} */
		const availableDates = await ctDateResponse.json();

		/** @type {Array<CalorieTrackerEntry>} */
		const ctList = await listCtForDateResponse.json();

		// add a blank entry for new input
		/** @type {CalorieTrackerEntry} */
		const blankEntry = {
			added: todayStr,
			amount: 0,
			category: Category.Unset
		};

		ctList.unshift(blankEntry);

		if (availableDates.indexOf(todayStr) < 0) {
			availableDates.unshift(todayStr);
		}

		return {
			availableDates: availableDates,
			entryToday: ctList
		};
	} else {
		return { error: 'An error has occured. Please try again later.' };
	}
};
