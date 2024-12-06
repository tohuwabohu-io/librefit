import { subDays } from 'date-fns';
import { listCalorieTrackerDatesRange, listCalorieTrackerRange } from '$lib/api/tracker.js';
import { getDateAsStr } from '$lib/date.js';

export const load = async ({ fetch }) => {
	const today = getDateAsStr(new Date());
	const fromDate = getDateAsStr(subDays(new Date(), 6));

	/** @type Array<String> */
	const trackedDaysWeek = await listCalorieTrackerDatesRange(fromDate, today);

	/** @type Array<CalorieTracker> */
	const calorieTrackerToday = await listCalorieTrackerRange(today, today);

	if (calorieTrackerToday && trackedDaysWeek) {
		return {
			availableDates: trackedDaysWeek,
			entryToday: calorieTrackerToday
		};
	} else {
		return { error: 'An error has occurred. Please try again later.' };
	}
};
