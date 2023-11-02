import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { getDateAsStr } from '$lib/util.js';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	const findLastWeightApi = api.findLastWeightTrackerEntry;
	const listWeightApi = api.listWeightTrackerEntriesRange;
	const goalApi = api.findLastGoal;

	const jwt = cookies.get('auth');

	const lastWeightResponse = await proxyFetch(fetch, findLastWeightApi, jwt);
	const goalResponse = await proxyFetch(fetch, goalApi, jwt);

	if (lastWeightResponse.status === 200) {
		const today = new Date();
		const fromDate = new Date();
		fromDate.setMonth(fromDate.getMonth() - 1);

		const listWeightResponse = await proxyFetch(fetch, listWeightApi, jwt, {
			dateFrom: getDateAsStr(fromDate),
			dateTo: getDateAsStr(today)
		});

		return {
			lastEntry: await lastWeightResponse.json(),
			entries: await listWeightResponse.json(),
			goal: await goalResponse.json()
		};
	} else {
		return {
			error: 'An error occured. Please try again later.'
		};
	}
};
