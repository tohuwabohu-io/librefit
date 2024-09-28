import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { getDateAsStr } from '$lib/date.js';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const today = new Date();

	// return dashboard relevant data
	const dashboardApi = api.getDashboard;

	/** @type Response */
	const dashboard = await proxyFetch(fetch, dashboardApi, { date: getDateAsStr(today) });

	if (dashboard.ok) {
		return {
			dashboardData: await dashboard.json()
		};
	}
};
