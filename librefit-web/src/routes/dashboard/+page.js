import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { subMonths, subWeeks } from 'date-fns';
import {
	listCaloriesForDate,
	listCalorieTrackerEntriesRange,
	listWeightForDate,
	listWeightRange
} from '$lib/api/tracker.js';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const userApi = api.readUserInfo;

	const today = new Date();
	const lastWeek = subWeeks(today, 1);
	const lastMonth = subMonths(today, 1);

	// return dashboard relevant data
	const goalApi = api.findLastGoal;
	const listFoodCategoriesApi = api.listFoodCategories;

	const user = await proxyFetch(fetch, userApi);
	const lastCtResponse = await listCaloriesForDate(today);
	const listWeightResponse = await listWeightRange(lastMonth, today);
	const listCtResponse = await listCalorieTrackerEntriesRange(lastWeek, today);

	const lastWeightResponse = await listWeightForDate(today);
	const lastGoalResponse = await proxyFetch(fetch, goalApi);
	const foodCategoryResponse = await proxyFetch(fetch, listFoodCategoriesApi);

	if (user.ok) {
		return {
			userData: await user.json(),
			currentGoal: lastGoalResponse.ok ? await lastGoalResponse.json() : undefined,
			foodCategories: await foodCategoryResponse.json(),
			lastWeight: lastWeightResponse.ok ? await lastWeightResponse.json() : undefined,
			lastCalories: lastCtResponse,
			listWeight: listWeightResponse.ok ? await listWeightResponse.json() : undefined,
			listCalories: listCtResponse ? await listCtResponse.json() : undefined
		};
	}
};
