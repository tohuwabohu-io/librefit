import { proxyFetch } from '$lib/server/api/util.js';
import { api } from '$lib/server/api/index.js';
import { getDateAsStr } from '$lib/util.js';
import { Category } from '$lib/api/model.js';

/** @type {import('./$types').LayoutServerLoad} */
export async function load({ fetch, cookies }) {
	const jwt = cookies.get('auth');
	const authenticated = jwt !== undefined && jwt !== 'undefined';

	if (authenticated) {
		const userApi = api.readUserInfo;

		const user = await proxyFetch(fetch, userApi, jwt);

		// return dashboard relevant data
		const lastWeightApi = api.findLastWeightTrackerEntry;
		const goalApi = api.findLastGoal;

		const lastWeightResponse = await proxyFetch(fetch, lastWeightApi, jwt);
		const lastGoalResponse = await proxyFetch(fetch, goalApi, jwt);

		if (user.ok) {
			return {
				authenticated: true,
				userData: await user.json(),
				lastWeight: lastWeightResponse.ok ? await lastWeightResponse.json() : undefined,
				currentGoal: lastGoalResponse.ok ? await lastGoalResponse.json() : undefined
			};
		}
	}

	return {
		authenticated: false
	};
}
