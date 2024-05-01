import { listCaloriesForDate, listCalorieTrackerDatesRange } from '$lib/api/tracker.js';
import { subDays } from 'date-fns';

export const load = async ({ fetch }) => {
	const today = new Date();
	const fromDate = subDays(today, 6);
	const ctDateResponse = await listCalorieTrackerDatesRange(fromDate, today);
	const listCtForDateResponse = await listCaloriesForDate(today);

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
