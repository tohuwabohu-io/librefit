import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';
import { isAfter } from 'date-fns';
import { parseStringAsDate } from '$lib/date.js';

/**
 * @param {CalorieTarget} calorieTarget
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
 * @param {WeightTarget} weightTarget
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

/**
 * @param target {CalorieTarget}
 * @returns {Object}
 */
export const validateCalorieTarget = (target) => {
	let endDateValidation = validateEndDate(target.startDate, target.endDate);
	let targetCaloriesValidation = validateTrackerAmount(target.targetCalories);
	let maximumCaloriesValidation = validateTrackerAmount(target.maximumCalories);

	if (maximumCaloriesValidation.valid) {
		maximumCaloriesValidation =
			target.targetCalories > target.maximumCalories
				? { valid: false, errorMessage: 'Maximum calories should be greater than target calories.' }
				: maximumCaloriesValidation;
	}

	return {
		endDate: endDateValidation,
		targetCalories: targetCaloriesValidation,
		maximumCalories: maximumCaloriesValidation
	};
};

/**
 * @param target {WeightTarget}
 * @returns {any}
 */
export const validateWeightTarget = (target) => {
	let endDateValidation = validateEndDate(target.startDate, target.endDate);
	let initialWeightMessage = validateTrackerAmount(target.initialWeight);
	let targetWeightMessage = validateTrackerAmount(target.targetWeight);

	return {
		endDate: endDateValidation,
		initialWeight: initialWeightMessage,
		targetWeight: targetWeightMessage
	};
};

export const validateEndDate = (startDateStr, endDateStr) => {
	let startDate = parseStringAsDate(startDateStr);
	let endDate = parseStringAsDate(endDateStr);

	if (isAfter(startDate, endDate)) {
		return {
			valid: false,
			errorMessage: 'End date must be after start date.'
		};
	}

	return { valid: true };
};

export const validateTrackerAmount = (detail) => {
	if (detail.value <= 0) {
		return {
			valid: false,
			errorMessage: `${detail.label} must be greater than zero.`
		};
	}

	return { valid: true };
};
