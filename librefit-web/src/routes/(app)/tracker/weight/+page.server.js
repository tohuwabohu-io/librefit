import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { getDateAsStr } from '$lib/util.js';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	const listWeightApi = api.listWeightTrackerEntriesRange;

	const jwt = cookies.get('auth');

	const today = new Date();
	const fromDate = new Date();
	fromDate.setMonth(fromDate.getMonth() - 1);

	const listWeightResponse = await proxyFetch(fetch, listWeightApi, jwt, {
		dateFrom: getDateAsStr(fromDate),
		dateTo: getDateAsStr(today)
	});

	return {
		entries: listWeightResponse.ok ? await listWeightResponse.json() : []
	};
};
