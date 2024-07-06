import { createGoal } from '$lib/api/target.js';
import { assert, describe, expect, it, vi } from 'vitest';

describe('target', () => {
	it('should make API call and handle responses correctly', async () => {
		// defining a mock goal object which will be used for testing
		const mockGoal = {
			added: '2022-08-12',
			targetCalories: 2000
		};

		// mocking successful API call response
		const mockApiResponseOk = new Response(
			new Blob([JSON.stringify(mockGoal)], { type: 'application/json' })
		);

		// mocking unsuccessful API call response
		const mockApiResponseNotOk = new Response(null, { status: 400 });

		// testing successful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseOk);
		const result = await createGoal(mockGoal);

		expect(result).toEqual(mockGoal);

		// testing unsuccessful API call
		global.fetch = vi.fn().mockResolvedValueOnce(mockApiResponseNotOk);

		try {
			await createGoal(mockGoal);
		} catch (error) {
			assert.strictEqual(error, mockApiResponseNotOk);
		}
	});
});
