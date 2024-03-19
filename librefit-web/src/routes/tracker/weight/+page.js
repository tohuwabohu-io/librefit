import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { getDateAsStr } from '$lib/util.js';

export const load = async ({ fetch }) => {
	const listWeightApi = api.listWeightTrackerEntriesRange;

	const today = new Date();
	const fromDate = new Date();
	fromDate.setMonth(fromDate.getMonth() - 1);

	const listWeightResponse = await proxyFetch(fetch, listWeightApi, {
		dateFrom: getDateAsStr(fromDate),
		dateTo: getDateAsStr(today)
	});

	return {
		entries: listWeightResponse.ok ? await listWeightResponse.json() : []
	};
};
