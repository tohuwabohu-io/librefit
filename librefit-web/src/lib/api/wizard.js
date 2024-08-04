import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';
import { getDateAsStr, parseStringAsDate } from '$lib/date.js';
import { CalculationGoal } from '$lib/api/model.js';
import { isAfter } from 'date-fns';

/**
 * @param wizard {Wizard}
 * @returns {Promise<void>}
 */
export const postWizardResult = async (wizard) => {
	return proxyFetch(fetch, api.postWizardResult, wizard).then(async (response) => {
		if (response.ok) {
			return Promise.resolve();
		} else throw response;
	});
};

/**
 * @param input
 * @returns {Promise<WizardTargetDateResult>}
 */
export const calculateForTargetDate = async (input) => {
	return proxyFetch(fetch, api.calculateForTargetDate, input).then(async (response) => {
		if (response.ok) {
			return await response.json();
		} else throw response;
	});
};

/**
 * @param input
 * @returns {Promise<WizardTargetWeightResult>}
 */
export const calculateForTargetWeight = async (input) => {
	return proxyFetch(fetch, api.calculateForTargetWeight, input).then(async (response) => {
		if (response.ok) {
			return await response.json();
		} else throw response;
	});
};

/**
 * @param wizardInput {WizardInput}
 * @param wizardResult {WizardResult}
 * @param customWizardResult {WizardTargetWeightResult}
 * @param startDate {Date}
 * @param targetWeight {Number}
 * @returns Map
 */
export const createTargetWeightTargets = (
	wizardInput,
	wizardResult,
	customWizardResult,
	startDate,
	targetWeight
) => {
	const rates = Object.keys(customWizardResult.datePerRate);
	const multiplier = wizardInput.calculationGoal === CalculationGoal.Loss ? -1 : 1;

	return rates.reduce((acc, rate) => {
		/** @type CalorieTarget */
		const calorieTarget = {
			startDate: getDateAsStr(startDate),
			endDate: customWizardResult.datePerRate[rate],
			targetCalories: wizardResult.target + multiplier * rate,
			maximumCalories: wizardResult.tdee
		};

		/** @type WeightTarget */
		const weightTarget = {
			startDate: getDateAsStr(startDate),
			endDate: customWizardResult.datePerRate[rate],
			initialWeight: wizardInput.weight,
			targetWeight: targetWeight
		};

		acc[rate] = { calorieTarget: calorieTarget, weightTarget: weightTarget };

		return acc;
	}, {});
};

/**
 * @param wizardInput {WizardInput}
 * @param wizardResult {WizardResult}
 * @param customWizardResult {WizardTargetDateResult}
 * @param startDate {Date}
 * @param endDate {Date}
 * @returns Map
 */
export const createTargetDateTargets = (
	wizardInput,
	wizardResult,
	customWizardResult,
	startDate,
	endDateStr
) => {
	const rates = Object.keys(customWizardResult.resultByRate);
	const multiplier = wizardInput.calculationGoal === CalculationGoal.Loss ? -1 : 1;

	return rates.reduce((acc, rate) => {
		/** @type WizardResult */
		const rateWizardResult = customWizardResult.resultByRate[rate];

		/** @type CalorieTarget */
		const calorieTarget = {
			startDate: getDateAsStr(startDate),
			endDate: endDateStr,
			targetCalories: wizardResult.tdee + multiplier * rate,
			maximumCalories: wizardResult.tdee
		};

		/** @type WeightTarget */
		const weightTarget = {
			startDate: getDateAsStr(startDate),
			endDate: endDateStr,
			initialWeight: wizardInput.weight,
			targetWeight: rateWizardResult.targetWeight
		};

		acc[rate] = { calorieTarget: calorieTarget, weightTarget: weightTarget };

		return acc;
	}, {});
};

export const validateCustomWeight = (detail) => {
	if (detail.value < 30 || detail.value > 300) {
		return {
			valid: false,
			errorMessage: 'Please provide a weight between 30kg and 300kg.'
		};
	}

	return { valid: true };
};

export const validateCustomDate = (detail) => {
	if (isAfter(new Date(), parseStringAsDate(detail.value))) {
		return {
			valid: false,
			errorMessage: 'Your target date lies in the past.'
		};
	}

	return { valid: true };
};
