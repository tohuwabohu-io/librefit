import { subMonths } from 'date-fns';
import { listCalorieTrackerRange } from '$lib/api/tracker.js';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const today = new Date();
	const lastMonth = subMonths(today, 1);

	const monthList = await listCalorieTrackerRange(lastMonth, today);

	if (monthList.ok) {
		return {
			caloriesMonthList: await monthList.json()
		};
	}
};
