import { getDateAsStr } from '$lib/date.js';
import { listCaloriesForDate, listCalorieTrackerDatesRange } from '$lib/api/tracker.js';
import { subDays } from 'date-fns';

export const load = async ({ fetch }) => {
	const today = new Date();
	const todayDateStr = getDateAsStr(today);
	const fromDateStr = getDateAsStr(subDays(today, 7));
	const ctDateResponse = await listCalorieTrackerDatesRange(fromDateStr, todayDateStr);
	const listCtForDateResponse = await listCaloriesForDate(todayDateStr);

	if (ctDateResponse.ok && listCtForDateResponse) {
		/** @type {Array<String>} */
		const availableDates = await ctDateResponse.json();

		return {
			availableDates: availableDates,
			entryToday: listCtForDateResponse
		};
	} else {
		return { error: 'An error has occured. Please try again later.' };
	}
};
