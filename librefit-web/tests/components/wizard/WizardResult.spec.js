import { cleanup, render, within } from '@testing-library/svelte';
import { afterEach, describe, expect, it } from 'vitest';
import WizardResult from '$lib/components/wizard/WizardResult.svelte';
import { BmiCategory, WizardRecommendation } from '$lib/api/model.js';
import { bmiCategoriesAsKeyValue } from '$lib/enum.js';

/**
 * @vitest-environment jsdom
 */
describe('WizardResult.svelte component', () => {
	afterEach(() => cleanup());

	it('should display underweight warning message for Underweight BMI category', async () => {
		const calculationInput = {
			age: 24,
			height: 180,
			weight: 60
		};

		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			bmi: 18,
			targetBmi: 23,
			bmiCategory: BmiCategory.Underweight,
			targetWeight: 70,
			targetWeightLower: 65,
			targetWeightUpper: 80,
			recommendation: WizardRecommendation.Gain
		};

		const { getByText } = render(WizardResult, { props: { calculationResult, calculationInput } });

		expect(getByText('underweight.')).toBeTruthy();
		expect(getByText('It is recommended to consult with a healthcare professional.')).toBeTruthy();
	});

	it('should display "overweight." for Overweight BMI category', async () => {
		const calculationInput = {
			age: 24,
			height: 180,
			weight: 80
		};

		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			bmi: 28,
			targetBmi: 23,
			bmiCategory: BmiCategory.Overweight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80,
			recommendation: WizardRecommendation.Lose
		};

		const { getByText } = render(WizardResult, { props: { calculationResult, calculationInput } });

		expect(getByText('overweight.')).toBeTruthy();
	});

	it('should display obesity warning message for Obese BMI category', async () => {
		const calculationInput = {
			age: 24,
			height: 180,
			weight: 90
		};

		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			bmi: 30,
			targetBmi: 23,
			bmiCategory: BmiCategory.Obese,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80,
			recommendation: WizardRecommendation.Lose
		};

		const { getByText } = render(WizardResult, { props: { calculationResult, calculationInput } });

		expect(getByText('obese.')).toBeTruthy();
		expect(getByText('It is recommended to consult with a healthcare professional.')).toBeTruthy();
	});

	it('should display severe obesity warning message for Severely_obese BMI category', async () => {
		const calculationInput = {
			age: 24,
			height: 180,
			weight: 100
		};

		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			bmi: 35,
			targetBmi: 23,
			bmiCategory: BmiCategory.Severely_obese,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80,
			recommendation: WizardRecommendation.Lose
		};

		const { getByText } = render(WizardResult, { props: { calculationResult, calculationInput } });

		expect(getByText('severely obese.')).toBeTruthy();
		expect(getByText('It is recommended to consult with a healthcare professional.')).toBeTruthy();
	});

	it('should display correct values in "Result" table', async () => {
		const calculationInput = {
			age: 24,
			height: 180,
			weight: 75
		};

		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			bmi: 23,
			targetBmi: 23,
			bmiCategory: BmiCategory.Standard_weight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80,
			recommendation: WizardRecommendation.Lose
		};

		const { getByRole } = render(WizardResult, { props: { calculationResult, calculationInput } });

		const yourResultTable = getByRole('table', { name: 'result table' });
		const utils = within(yourResultTable);
		expect(utils.getByText(calculationResult.bmr.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.tdee.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.deficit.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.target.toString()).closest('td')).toBeTruthy();
	});

	it('should display correct values in "Body parameters" table', () => {
		const calculationInput = {
			age: 24,
			height: 180,
			weight: 75
		};

		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			bmi: 23,
			targetBmi: 23,
			bmiCategory: BmiCategory.Standard_weight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80,
			recommendation: WizardRecommendation.Hold
		};

		const bmiCategoryLabel = bmiCategoriesAsKeyValue.filter(
			(e) => e.value === calculationResult.bmiCategory
		)[0].label;

		const { getByRole } = render(WizardResult, { props: { calculationResult, calculationInput } });

		const bodyParametersTable = getByRole('table', { name: 'body parameter table' });
		const utils = within(bodyParametersTable);
		expect(utils.getByText(calculationInput.age.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationInput.height.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationInput.weight.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.bmi.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(bmiCategoryLabel).closest('td')).toBeTruthy();
	});
});
