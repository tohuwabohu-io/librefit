import { subDays } from 'date-fns';
import { listWeightRange } from '$lib/api/tracker.js';

export const load = async ({ fetch }) => {
	const today = new Date();
	const fromDate = subDays(today, 6);
	const wtListResponse = await listWeightRange(fromDate, today);

	if (wtListResponse.ok) {
		return {
			weightWeekList: await wtListResponse.json()
		};
	} else {
		return { error: 'An error has occured. Please try again later.' };
	}
};
