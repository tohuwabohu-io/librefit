import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';

/**
 * @param {Goal} goal
 * @return {Promise}
 */
export const createGoal = async (goal) => {
	return proxyFetch(fetch, api.createGoal, goal).then(async (response) => {
		if (response.ok) {
			const goal = await response.json();

			return Promise.resolve(goal);
		} else throw response;
	});
};
