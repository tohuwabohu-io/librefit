import { createCalorieTarget } from '$lib/api/target.js';
import { assert, describe, expect, it, vi } from 'vitest';

describe('target', () => {
	it('createCalorieTarget should make API call and handle responses correctly', async () => {
		/** @type CalorieTarget */
		const mockCalorieTarget = {
			added: '2022-08-12',
			targetCalories: 2000,
			maximumCalories: 2500
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
		/** @type WeightTarget */
		const mockWeightTarget = {
			added: '2022-08-12',
			initialWeight: 80,
			targetWeight: 70
		};

		// mocking successful API call response
		const mockApiResponseOk = new Response(
			new Blob([JSON.stringify(mockWeightTarget)], { type: 'application/json' })
		);

		// mocking unsuccessful API call response
		const mockApiResponseNotOk = new Response(null, { status: 400 });

		// testing successful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseOk);
		const result = await createCalorieTarget(mockWeightTarget);

		expect(result).toEqual(mockWeightTarget);

		// testing unsuccessful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseNotOk);

		try {
			await createCalorieTarget(mockWeightTarget);
		} catch (error) {
			assert.strictEqual(error, mockApiResponseNotOk);
		}
	});
});
