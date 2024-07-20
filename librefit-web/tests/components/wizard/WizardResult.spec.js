import { cleanup, render, within } from '@testing-library/svelte';
import { afterEach, describe, expect, it } from 'vitest';
import WizardResult from '$lib/components/wizard/WizardResult.svelte';
import { BmiCategory } from '$lib/api/model.js';
import { bmiCategoriesAsKeyValue } from '$lib/enum.js';

/**
 * @vitest-environment jsdom
 */

describe('WizardResult.svelte component', () => {
	afterEach(() => cleanup());

	it('should display "currently in the optimal weight range" for Standard_weight BMI category', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 75,
			bmi: 23,
			bmiCategory: BmiCategory.Standard_weight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80
		};

		const { getByText } = render(WizardResult, { props: { calculationResult } });

		expect(
			getByText(`Being standard weight, you are currently in the optimal weight range.`)
		).toBeTruthy();
	});

	it('should display "You are underweight" for Underweight BMI category', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 60,
			bmi: 18,
			bmiCategory: BmiCategory.Underweight,
			targetWeight: 70,
			targetWeightLower: 65,
			targetWeightUpper: 80
		};

		const { getByText } = render(WizardResult, { props: { calculationResult } });

		expect(
			getByText(
				'You are underweight. First of all, it is recommended to consult with a healthcare professional.'
			)
		).toBeTruthy();
	});

	it('should display "You are Overweight" for Overweight BMI category', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 80,
			bmi: 28,
			bmiCategory: BmiCategory.Overweight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80
		};

		const { getByText } = render(WizardResult, { props: { calculationResult } });

		expect(getByText('You are overweight.')).toBeTruthy();
	});

	it('should display "You are obese" for Obese BMI category', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 90,
			bmi: 30,
			bmiCategory: BmiCategory.Obese,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80
		};

		const { getByText } = render(WizardResult, { props: { calculationResult } });

		expect(getByText('You are obese. Please consult with a healthcare professional.')).toBeTruthy();
	});

	it('should display "You are severely obese" for Severely_obese BMI category', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 100,
			bmi: 35,
			bmiCategory: BmiCategory.Severely_obese,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80
		};

		const { getByText } = render(WizardResult, { props: { calculationResult } });

		expect(
			getByText('You are severely obese. Please consult with a healthcare professional.')
		).toBeTruthy();
	});

	it('should display correct values in "Your result" table', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 75,
			bmi: 23,
			bmiCategory: BmiCategory.Standard_weight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80
		};

		const { getByRole } = render(WizardResult, { props: { calculationResult } });

		const yourResultTable = getByRole('table', { name: 'result table' });
		const utils = within(yourResultTable);
		expect(utils.getByText(calculationResult.bmr.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.tdee.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.deficit.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.target.toString()).closest('td')).toBeTruthy();
	});

	it('should display correct values in "Body parameters" table', async () => {
		const calculationResult = {
			bmr: 1500,
			tdee: 2500,
			deficit: 500,
			target: 2000,
			age: 24,
			height: 180,
			weight: 75,
			bmi: 23,
			bmiCategory: BmiCategory.Standard_weight,
			targetWeight: 70,
			targetWeightLower: 60,
			targetWeightUpper: 80
		};

		const bmiCategoryLabel = bmiCategoriesAsKeyValue.filter(
			(e) => e.value === calculationResult.bmiCategory
		)[0].label;

		const { getByRole } = render(WizardResult, { props: { calculationResult } });

		const bodyParametersTable = getByRole('table', { name: 'body parameter table' });
		const utils = within(bodyParametersTable);
		expect(utils.getByText(calculationResult.age.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.height.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.weight.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(calculationResult.bmi.toString()).closest('td')).toBeTruthy();
		expect(utils.getByText(bmiCategoryLabel).closest('td')).toBeTruthy();
	});
});
