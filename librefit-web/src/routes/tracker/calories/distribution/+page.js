import { subMonths } from 'date-fns';
import { listCalorieTrackerEntriesRange } from '$lib/api/tracker.js';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const today = new Date();
	const lastMonth = subMonths(today, 1);

	/** @type Promise<List<CalorieTrackerEntry>> */
	const monthList = listCalorieTrackerEntriesRange(lastMonth, today);

	if (monthList.ok) {
		return {
			caloriesMonthList: await monthList.json()
		};
	}
};
