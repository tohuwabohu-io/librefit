import { invoke } from '@tauri-apps/api/core';

/**
 * @param calorieTarget {CalorieTarget}
 * @return {Promise<* | undefined>}
 */
export const createCalorieTarget = async (calorieTarget) => {
	calorieTarget.targetCalories = +calorieTarget.targetCalories;
	calorieTarget.maximumCalories = +calorieTarget.maximumCalories;

	return invoke('create_calorie_target', { newTarget: calorieTarget });
};

/**
 * @param weightTarget {WeightTarget}
 * @return {Promise<* | undefined>}
 */
export const createWeightTarget = async (weightTarget) => {
	weightTarget.targetWeight = +weightTarget.targetWeight;
	weightTarget.initialWeight = +weightTarget.initialWeight;
	
	return invoke('create_weight_target', { newTarget: weightTarget });
};
