import { getDateAsStr } from '$lib/date.js';
import { listWeightRange } from '$lib/api/tracker.js';

export const load = async ({ fetch }) => {
	const today = new Date();
	const fromDate = new Date();
	fromDate.setMonth(fromDate.getMonth() - 1);

	const listWeightResponse = await listWeightRange(
		getDateAsStr(fromDate), 
		getDateAsStr(today) 
	);

	return {
		entries: listWeightResponse
	};
};
