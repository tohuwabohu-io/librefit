import { getAverageCategoryIntake } from '$lib/calorie-util.ts';
import { getAverageDailyIntake } from '$lib/calorie-util.ts';
import { describe, expect, it } from 'vitest';

describe('getAverageCategoryIntake', () => {
	it('should return a map of average daily intake per category', async () => {
		const entries = [
			{ category: 'fruit', amount: 100, added: '2024-01-01' },
			{ category: 'vegetables', amount: 200, added: '2024-01-01' },
			{ category: 'fruit', amount: 50, added: '2024-01-02' },
			{ category: 'dairy', amount: 150, added: '2024-01-01' }
		];
		const foodCategories = [
			{ shortvalue: 'fruit' },
			{ shortvalue: 'vegetables' },
			{ shortvalue: 'dairy' }
		];

		const result = getAverageCategoryIntake(entries, foodCategories);

		// Verify the function returns correct value
		expect(result.get('fruit')).toBe(75);
		expect(result.get('vegetables')).toBe(100);
		expect(result.get('dairy')).toBe(75);
	});

	it('should return null if no entries', () => {
		const result = getAverageCategoryIntake([], []);
		expect(result).toBeNull();
	});
});

describe('getAverageDailyIntake', () => {
	it('should return average daily intake from a list of entries', async () => {
		const entries = [
			{ amount: 100, added: '2024-01-01' },
			{ amount: 200, added: '2024-01-01' },
			{ amount: 50, added: '2024-01-02' },
			{ amount: 150, added: '2024-01-03' }
		];

		const result = getAverageDailyIntake(entries);

		// sum / 3 (rounded)
		expect(result).toBe(167);
	});

	it('should return 0 if no entries', () => {
		const result = getAverageDailyIntake([]);
		expect(result).toBe(0);
	});

	it('should return 0 if all entries have 0 amount', () => {
		const entries = [
			{ amount: 0, added: '2024-01-01' },
			{ amount: 0, added: '2024-01-02' }
		];
		const result = getAverageDailyIntake(entries);
		expect(result).toBe(0);
	});
});
