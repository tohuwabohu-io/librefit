import { assert, describe, expect, it, vi } from 'vitest';
import { CalorieTarget, NewCalorieTarget, NewWeightTarget, WeightTarget } from '../../../src/lib/model';
import { createCalorieTarget, createWeightTarget } from '../../../src/lib/api/target';
import {
	validateCalorieTarget,
	validateEndDate,
	validateTargetAmount,
	validateWeightTarget
} from '../../../src/lib/validation';

describe('createTarget functions', () => {
	it('createCalorieTarget should make API call and handle responses correctly', async () => {
		const mockCalorieTarget: NewCalorieTarget = {
			added: '2022-08-12',
			targetCalories: 2000,
			maximumCalories: 2500,
			startDate: '2025-01-01',
			endDate: '2025-12-31'
		};

		// mocking successful API call response
		const mockApiResponseOk = new Response(
			new Blob([JSON.stringify(mockCalorieTarget)], { type: 'application/json' })
		);

		// mocking unsuccessful API call response
		const mockApiResponseNotOk = new Response(null, { status: 400 });

		// testing successful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseOk);
		const result = await createCalorieTarget(mockCalorieTarget);

		expect(result).toEqual(mockCalorieTarget);

		// testing unsuccessful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseNotOk);

		try {
			await createCalorieTarget(mockCalorieTarget);
		} catch (error) {
			assert.strictEqual(error, mockApiResponseNotOk);
		}
	});

	it('createWeightTarget should make API call and handle responses correctly', async () => {
		const mockWeightTarget: NewWeightTarget = {
			added: '2022-08-12',
			initialWeight: 80,
			targetWeight: 70,
			startDate: '2025-01-01',
			endDate: '2025-12-31'
		};

		// mocking successful API call response
		const mockApiResponseOk = new Response(
			new Blob([JSON.stringify(mockWeightTarget)], { type: 'application/json' })
		);

		// mocking unsuccessful API call response
		const mockApiResponseNotOk = new Response(null, { status: 400 });

		// testing successful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseOk);
		const result = await createWeightTarget(mockWeightTarget);

		expect(result).toEqual(mockWeightTarget);

		// testing unsuccessful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseNotOk);

		try {
			await createWeightTarget(mockWeightTarget);
		} catch (error) {
			assert.strictEqual(error, mockApiResponseNotOk);
		}
	});
});

describe('validateEndDate', () => {
	it('should return valid for start date before end date', () => {
		const result = validateEndDate('2022-01-01', '2023-01-01');
		expect(result.valid).toBeTruthy();
		expect(result.errorMessage).toBeUndefined();
	});

	it('should return invalid for end date before start date', () => {
		const result = validateEndDate('2023-01-01', '2022-01-01');
		expect(result.valid).toBeFalsy();
		assert.strictEqual(result.errorMessage, 'End date must be after start date.');
	});
});

describe('validateTrackerAmount', () => {
	it('should return valid when detail value is greater than zero', () => {
		const result = validateTargetAmount({ value: 10, label: 'Weight' });
		expect(result.valid).toBeTruthy();
		expect(result.errorMessage).toBeUndefined();
	});

	it('should return invalid when detail value is less than or equal to zero', () => {
		const result = validateTargetAmount({ value: 0, label: 'Weight' });
		expect(result.valid).toBeFalsy();
		assert.strictEqual(result.errorMessage, 'Weight must be greater than zero.');
	});
});

describe('validateTarget functions', () => {
	it('should correctly validate Calorie and Weight targets', () => {
		const mockCalorieTarget: CalorieTarget = {
			id: 1,
			added: '2022-08-12',
			targetCalories: 2000,
			maximumCalories: 2500,
			startDate: '2022-08-12',
			endDate: '2023-08-12'
		};

		const mockWeightTarget: WeightTarget = {
			id: 1,
			added: '2022-08-12',
			initialWeight: 80,
			targetWeight: 70,
			startDate: '2022-08-12',
			endDate: '2023-08-12'
		};

		// testing CalorieTarget validation
		const calorieTargetValidationResult = validateCalorieTarget(mockCalorieTarget);
		assert.strictEqual(calorieTargetValidationResult.endDate.valid, true);
		assert.strictEqual(calorieTargetValidationResult.targetCalories.valid, true);
		assert.strictEqual(calorieTargetValidationResult.maximumCalories.valid, true);

		// testing WeightTarget validation
		const weightTargetValidationResult = validateWeightTarget(mockWeightTarget);
		assert.strictEqual(weightTargetValidationResult.endDate.valid, true);
		assert.strictEqual(weightTargetValidationResult.initialWeight.valid, true);
		assert.strictEqual(weightTargetValidationResult.targetWeight.valid, true);
	});
});
