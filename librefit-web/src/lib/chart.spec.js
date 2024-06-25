import { createWeightChart, createWeightChartDataset, paintWeightTrackerEntries } from './chart';
import { describe, expect, it, vi } from 'vitest';
import { DataViews } from '$lib/enum.js';

describe('createWeightChart function', () => {
	it('should create data and legend for a week', () => {
		// We will use a simplified approximation of the real data for brevity
		const start = new Date(2024, 7, 1);
		const entries = [
			{ added: '2024-08-01', amount: 70 },
			{ added: '2024-08-02', amount: 71 },
			{ added: '2024-08-03', amount: 70 },
			{ added: '2024-08-04', amount: 69 },
			{ added: '2024-08-05', amount: 70 },
			{ added: '2024-08-06', amount: 70 }
		];

		const chart = createWeightChart(DataViews.Week, start, entries);
		expect(chart.data.length).toBe(7); // 'Data contains one week of entries'
		expect(chart.legend.length).toBe(7); // 'Legend contains one week of entries'

		const chartEmpty = createWeightChart(DataViews.Week, start, []);
		expect(chartEmpty.data.length).toBe(7); // 'Empty data contains one week of entries'
		expect(chartEmpty.legend.length).toBe(7); // 'Empty legend contains one week of entries'
		expect(chartEmpty.data.every((value) => Number.isNaN(value))).toBe(true); // 'Empty data entries are NaN'
	});

	it('should create data and legend for a month', async () => {
		// We will use a simplified approximation of the real data for brevity
		const start = new Date(2024, 7, 1);
		const entries = Array.from({ length: 31 }, (_, i) => ({
			added: `2024-08-${String(i + 1).padStart(2, '0')}`,
			amount: 70 + i
		}));

		const chart = createWeightChart(DataViews.Month, start, entries);
		expect(chart.data.length).toBe(31); // 'Data contains one month of entries'
		expect(chart.legend.length).toBe(31); // 'Legend contains one month of entries'
	});

	it('should create data and legend for a year', async () => {
		// We will use a simplified approximation of the real data for brevity
		const start = new Date(2024, 0, 1);
		const entries = Array.from({ length: 12 }, (_, i) => ({
			added: `2024-${String(i + 1).padStart(2, '0')}-15`,
			amount: 70 + i
		}));

		const chart = createWeightChart(DataViews.Year, start, entries);
		expect(chart.data.length).toBe(12); // 'Data contains one year of entries'
		expect(chart.legend.length).toBe(12); // 'Legend contains one year of entries'
	});
});

/**
 * @vitest-environment jsdom
 */
describe('createWeightChartDataset function', async () => {
	it('should create a weight chart dataset with dark theme', () => {
		// Mock getComputedStyle
		window.getComputedStyle = vi.fn().mockReturnValue({
			getPropertyValue: (prop) => {
				if (prop === '--color-surface-500') return '255, 255, 255';
				if (prop === '--color-surface-200') return '0, 0, 0';
			}
		});

		// Simulate weight data
		const weight = [70, 71, 70, 69, 70, 70];

		// Add 'dark' class to documentElement
		document.documentElement.classList.add('dark');

		const chartDataset = createWeightChartDataset(weight);

		expect(chartDataset.borderColor).toBe('rgb(0, 0, 0)');
		expect(chartDataset.label).toBe('Weight (kg)');
		expect(chartDataset.data).toBe(weight);
		expect(chartDataset.label).toBe('Weight (kg)');
		expect(chartDataset.options).toEqual({
			fill: false,
			interaction: {
				intersect: false
			},
			radius: 0,
			tension: 1
		});
		expect(chartDataset.spanGaps).toBeTruthy();
	});

	it('should create a weight chart dataset with light theme', async () => {
		// Mock getComputedStyle
		window.getComputedStyle = vi.fn().mockReturnValue({
			getPropertyValue: (prop) => {
				if (prop === '--color-surface-500') return '255, 255, 255';
				if (prop === '--color-surface-200') return '0, 0, 0';
			}
		});

		// Simulate weight data
		const weight = [70, 71, 70, 69, 70, 70];

		// Make sure 'dark' class is not present
		document.documentElement.classList.remove('dark');

		const chartDataset = createWeightChartDataset(weight);

		expect(chartDataset.borderColor).toBe('rgb(255, 255, 255)');
		expect(chartDataset.label).toBe('Weight (kg)');
		expect(chartDataset.data).toBe(weight);
		expect(chartDataset.label).toBe('Weight (kg)');
		expect(chartDataset.options).toEqual({
			fill: false,
			interaction: {
				intersect: false
			},
			radius: 0,
			tension: 1
		});
		expect(chartDataset.spanGaps).toBeTruthy();
	});
});

/**
 * @vitest-environment jsdom
 */
describe('paintWeightTrackerEntries function', () => {
	it('should paint weight tracker entries with dark theme', async () => {
		// Mock getComputedStyle
		window.getComputedStyle = vi.fn().mockReturnValue({
			getPropertyValue: (prop) =>
				({
					'--color-surface-100': '0,0,255',
					'--color-surface-200': '0,255,0',
					'--color-surface-500': '255,0,0',
					'--color-surface-800': '0,255,255',
					'--color-surface-900': '255,0,255'
				})[prop]
		});

		// Simulate weight data
		const entries = Array.from({ length: 12 }, (_, i) => ({
			added: `2024-${String(i + 1).padStart(2, '0')}-15`,
			amount: 70 + i
		}));

		const date = new Date(2024, 0, 15);

		// Add 'dark' class to documentElement
		document.documentElement.classList.add('dark');

		const chart = paintWeightTrackerEntries(entries, date, DataViews.Month);

		expect(chart.chartData.datasets[0].label).toBe('Weight (kg)'); // Ensuring a dataset got created
		expect(chart.chartOptions.scales.y.suggestedMin).toBe(67.5); // Min 2.5 less than the smallest weight
		expect(chart.chartOptions.scales.y.suggestedMax).toBe(83.5); // Max 2.5 more than the largest weight
		expect(chart.chartOptions.scales.y.ticks.color).toBe('rgb(0,0,255)'); // Check colors
		expect(chart.chartOptions.scales.x.ticks.color).toBe('rgb(0,0,255)');
		expect(chart.chartOptions.plugins.legend.labels.color).toBe('rgb(0,0,255)');
	});

	it('should paint weight tracker entires with light theme', () => {
		// Mock getComputedStyle
		window.getComputedStyle = vi.fn().mockReturnValue({
			getPropertyValue: (prop) =>
				({
					'--color-surface-100': '0,0,255',
					'--color-surface-200': '0,255,0',
					'--color-surface-500': '255,0,0',
					'--color-surface-800': '0,255,255',
					'--color-surface-900': '255,0,255'
				})[prop]
		});

		// Simulate weight data
		const entries = Array.from({ length: 12 }, (_, i) => ({
			added: `2024-${String(i + 1).padStart(2, '0')}-15`,
			amount: 70 + i
		}));

		const date = new Date(2024, 0, 15);

		// Ensure 'dark' class is not present
		document.documentElement.classList.remove('dark');

		const chart = paintWeightTrackerEntries(entries, date, DataViews.Month);

		expect(chart.chartData.datasets[0].label).toBe('Weight (kg)'); // Ensuring a dataset got created
		expect(chart.chartOptions.scales.y.suggestedMin).toBe(67.5); // Min 2.5 less than the smallest weight
		expect(chart.chartOptions.scales.y.suggestedMax).toBe(83.5); // Max 2.5 more than the largest weight

		// Check colors - They should be using 'light' theme colors
		expect(chart.chartOptions.scales.y.ticks.color).toBe('rgb(255,0,255)');
		expect(chart.chartOptions.scales.x.ticks.color).toBe('rgb(255,0,255)');
		expect(chart.chartOptions.plugins.legend.labels.color).toBe('rgb(255,0,255)');
	});
});
