import { subMonths } from 'date-fns';
import { listCalorieTrackerRange } from '$lib/api/tracker.ts';
import { getDateAsStr } from '$lib/date.ts';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const today = new Date();
	const lastMonth = subMonths(today, 1);

	const monthList = await listCalorieTrackerRange(getDateAsStr(lastMonth), getDateAsStr(today));

	return {
		caloriesMonthList: monthList
	};
};
