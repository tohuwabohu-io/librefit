import { expect, describe, it, vi } from 'vitest';
import { paintCalorieTrackerQuickview } from '$lib/quickview-chart.js';

/**
 * @vitest-environment jsdom
 */
describe('paintCalorieTrackerQuickview', () => {
	const entry1 = { amount: 1700, added: '2024-01-01', sequence: 1 };
	const entry2 = { amount: 1900, added: '2024-02-01', sequence: 1 };
	const entry3 = { amount: 2300, added: '2024-03-02', sequence: 1 };
	const goal = { targetCalories: 1800, maximumCalories: 2200 };

	it('should return an object with chartData and chartOptions for input entries', () => {
		const result = paintCalorieTrackerQuickview([entry1, entry2], goal);
		expect(result).toHaveProperty('chartData');
		expect(result).toHaveProperty('chartOptions');
	});

	it('should return an object with undefined chartData and chartOptions for empty entries', () => {
		const entries = [];
		const goal = 100;
		const result = paintCalorieTrackerQuickview(entries, goal);
		expect(result).toEqual({ chartData: undefined, chartOptions: undefined });
	});

	it('should correctly classify and style entries based on comparison with goal properties', () => {
		// Mock getComputedStyle
		window.getComputedStyle = vi.fn().mockReturnValue({
			getPropertyValue: (prop) => {
				if (prop === '--color-surface-500') return '255, 255, 255';
				if (prop === '--color-surface-200') return '128, 128, 128';
				if (prop === '--color-primary-500') return '64, 64, 64';
				if (prop === '--color-warning-500') return '32, 32, 32';
				if (prop === '--color-error-500') return '16, 16, 16';
			}
		});

		// check light theme
		document.documentElement.classList.remove('dark');

		let quickview = paintCalorieTrackerQuickview([entry1, entry2, entry3], goal);
		let lineChart = quickview.chartData.datasets[0];
		let barChart = quickview.chartData.datasets[1];

		expect(lineChart.borderColor).toEqual('rgb(255, 255, 255)');

		expect(barChart.backgroundColor[0]).toEqual('rgb(64, 64, 64 / .7)');
		expect(barChart.backgroundColor[1]).toEqual('rgb(32, 32, 32 / .7)');
		expect(barChart.backgroundColor[2]).toEqual('rgb(16, 16, 16 / .7)');

		expect(barChart.borderColor[0]).toEqual('rgb(128, 128, 128)');

		// check dark theme
		document.documentElement.classList.add('dark');

		quickview = paintCalorieTrackerQuickview([entry1, entry2, entry3], goal);
		lineChart = quickview.chartData.datasets[0];
		barChart = quickview.chartData.datasets[1];

		expect(lineChart.borderColor).toEqual('rgb(128, 128, 128)');

		expect(barChart.backgroundColor[0]).toEqual('rgb(64, 64, 64 / .7)');
		expect(barChart.backgroundColor[1]).toEqual('rgb(32, 32, 32 / .7)');
		expect(barChart.backgroundColor[2]).toEqual('rgb(16, 16, 16 / .7)');

		expect(barChart.borderColor[0]).toEqual('rgb(255, 255, 255)');
	});

	it('should return object of correct shape', () => {
		const result = paintCalorieTrackerQuickview([entry1, entry2], goal);

		// Validate the shape of the returned object
		expect(result).toHaveProperty('chartData.labels');
		expect(result).toHaveProperty('chartData.datasets');
		expect(result).toHaveProperty('chartOptions.indexAxis');
		expect(result).toHaveProperty('chartOptions.scales');
		expect(result).toHaveProperty('chartOptions.responsive');
		expect(result).toHaveProperty('chartOptions.aspectRatio');

		// Further assertions can be made on the length and type of properties
		expect(Array.isArray(result.chartData.labels)).toBeTruthy();
		expect(Array.isArray(result.chartData.datasets)).toBeTruthy();
	});
});
