import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';

/**
 * @param calorieTarget {CalorieTarget}
 * @return {Promise<* | undefined>}
 */
export const createCalorieTarget = async (calorieTarget) => {
	return proxyFetch(fetch, api.createCalorieTarget, calorieTarget).then(async (response) => {
		if (response.ok) {
			const calorieTarget = await response.json();

			return Promise.resolve(calorieTarget);
		} else throw response;
	});
};

/**
 * @param weightTarget {WeightTarget}
 * @return {Promise<* | undefined>}
 */
export const createWeightTarget = async (weightTarget) => {
	return proxyFetch(fetch, api.createWeightTarget, weightTarget).then(async (response) => {
		if (response.ok) {
			const weightTarget = await response.json();

			return Promise.resolve(weightTarget);
		} else throw response;
	});
};
