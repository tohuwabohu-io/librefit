import { getDateAsStr, parseStringAsDate } from '../date';
import {
	CalculationGoal,
	CalorieTarget,
	NewCalorieTarget,
	NewWeightTarget,
	WeightTarget,
	Wizard,
	WizardInput,
	WizardResult, WizardTargetDateInput,
	WizardTargetDateResult, WizardTargetWeightInput,
	WizardTargetWeightResult
} from '../model';
import { isAfter } from 'date-fns';
import { invoke } from '@tauri-apps/api/core';

export const calculateTdee = async (wizardInput: WizardInput): Promise<WizardResult> => {
	wizardInput.age = +wizardInput.age;
	wizardInput.weight = +wizardInput.weight;
	wizardInput.height = +wizardInput.height;
	wizardInput.weeklyDifference = +wizardInput.weeklyDifference;

	return invoke('wizard_calculate_tdee', {
		input: wizardInput
	});
};

/**
 * @param wizard {Wizard}
 * @returns {Promise<void>}
 */
export const postWizardResult = async (wizard: Wizard): Promise<void> => {
	return invoke('wizard_create_targets', {
		input: wizard
	});
};

export const calculateForTargetDate = async (wizardTargetDateInput: WizardTargetDateInput): Promise<WizardTargetDateResult> => {
	return invoke('wizard_calculate_for_target_date', {
		input: wizardTargetDateInput
	});
};

export const calculateForTargetWeight = async (wizardTargetDateInput: WizardTargetWeightInput): Promise<WizardTargetWeightResult> => {
	return invoke('wizard_calculate_for_target_weight', {
		input: wizardTargetDateInput
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
	wizardInput: WizardInput,
	wizardResult: WizardResult,
	customWizardResult: WizardTargetWeightResult,
	startDate: Date,
	targetWeight: number
): Map<string, { calorieTarget: CalorieTarget; weightTarget: WeightTarget }> => {
	const rates = Object.keys(customWizardResult.datePerRate);
	const multiplier = targetWeight < wizardInput.weight ? -1 : 1;
	const todayStr = getDateAsStr(new Date());

	return rates.reduce((acc, rate) => {
		const calorieTarget: NewCalorieTarget = {
			added: todayStr,
			startDate: getDateAsStr(startDate),
			endDate: customWizardResult.datePerRate[rate],
			targetCalories: wizardResult.tdee + multiplier * parseFloat(rate),
			maximumCalories: wizardResult.tdee
		};

		const weightTarget: NewWeightTarget = {
			added: todayStr,
			startDate: getDateAsStr(startDate),
			endDate: customWizardResult.datePerRate[rate],
			initialWeight: wizardInput.weight,
			targetWeight: targetWeight
		};

		acc.set(rate, { calorieTarget, weightTarget });

		return acc;
	}, new Map());
};

export const createTargetDateTargets = (
	wizardInput: WizardInput,
	wizardResult: WizardResult,
	customWizardResult: WizardTargetDateResult,
	startDate: Date,
	endDateStr: string
): Map<string, { calorieTarget: CalorieTarget; weightTarget: WeightTarget }> => {
	const rates = Object.keys(customWizardResult.datePerRate);
	const multiplier = wizardInput.calculationGoal === CalculationGoal.Loss ? -1 : 1;
	const todayStr = getDateAsStr(new Date());

	return rates.reduce((acc, rate) => {
		const rateWizardResult = customWizardResult.datePerRate[rate] as WizardResult;

		const calorieTarget: NewCalorieTarget = {
			added: todayStr,
			startDate: getDateAsStr(startDate),
			endDate: endDateStr,
			targetCalories: wizardResult.tdee + multiplier * parseFloat(rate),
			maximumCalories: wizardResult.tdee
		};

		const weightTarget: NewWeightTarget = {
			added: todayStr,
			startDate: getDateAsStr(startDate),
			endDate: endDateStr,
			initialWeight: wizardInput.weight,
			targetWeight: rateWizardResult.targetWeight
		};

		acc.set(rate, { calorieTarget, weightTarget });

		return acc;
	}, new Map());
};

export const validateCustomWeight = (detail: { value: number }): { valid: boolean; errorMessage?: string } => {
	if (detail.value < 30 || detail.value > 300) {
		return {
			valid: false,
			errorMessage: 'Please provide a weight between 30kg and 300kg.'
		};
	}

	return { valid: true };
};

export const validateCustomDate = (detail: { value: string }): { valid: boolean; errorMessage?: string } => {
	if (isAfter(new Date(), parseStringAsDate(detail.value))) {
		return {
			valid: false,
			errorMessage: 'Your target date lies in the past.'
		};
	}

	return { valid: true };
};
