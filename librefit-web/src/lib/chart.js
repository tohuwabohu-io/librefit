import { format, sub } from 'date-fns';
import { enGB } from 'date-fns/locale';
import { DataViews } from '$lib/enum.js';
import { display_date_format_day, getDateAsStr, parseStringAsDate } from '$lib/date.js';

/**
 * @param {DataViews} view
 * @param {Date} start
 * @param {Array<WeightTrackerEntry>} entries
 */
export const createWeightChart = (view, start, entries) => {
	const legend = [];
	const data = [];
	const begin = 1;
	let end;
	let displayFormat;
	let duration;
	switch (view) {
		case DataViews.Year:
			end = 12;
			displayFormat = 'LLLL yyyy';
			duration = { months: 1 };
			break;
		case DataViews.Month:
			end = new Date(start.getFullYear(), start.getMonth(), 0).getDate();
			displayFormat = 'dd.LL';
			duration = { days: 1 };
			break;
		case DataViews.Week:
			end = 7;
			displayFormat = 'EEEEEE';
			duration = { days: 1 };
			break;
		default:
			end = 1;
			displayFormat = 'dd';
			duration = {};
			break;
	}
	let tmpDate = start;
	let tmpDateStr = '';
	for (let i = begin, j = end - 1; i <= end; i++, j--) {
		tmpDateStr = getDateAsStr(tmpDate);
		const values = entries
			.filter((entry) => {
				const entryAdded = parseStringAsDate(entry.added);
				let filterPassed = false;
				switch (view) {
					case DataViews.Year:
						filterPassed =
							entryAdded.getFullYear() === tmpDate.getFullYear() &&
							entryAdded.getMonth() === tmpDate.getMonth();
						break;
					case DataViews.Month:
					case DataViews.Week:
						filterPassed = entry.added === tmpDateStr;
						break;
				}
				return filterPassed;
			})
			.map((entry) => entry.amount);
		if (values.length > 0) {
			const sum = values.reduce((a, b) => {
				return a + b;
			});
			if (sum) {
				data[j] = sum / values.length;
			} else {
				data[j] = NaN;
			}
		} else {
			data[j] = NaN;
		}
		legend[j] = format(tmpDate, displayFormat.valueOf(), { locale: enGB });
		tmpDate = sub(tmpDate, duration);
	}
	return { legend, data };
};

/**
 * @param {any} weight
 */
export const createWeightChartDataset = (weight) => {
	const style = getComputedStyle(document.body);
	const elemHtmlClasses = document.documentElement.classList;

	let lineColor = style.getPropertyValue('--color-surface-500');

	if (elemHtmlClasses.contains('dark')) {
		lineColor = style.getPropertyValue('--color-surface-200');
	}

	return {
		label: 'Weight (kg)',
		borderColor: `rgb(${lineColor})`,
		options: {
			fill: false,
			interaction: {
				intersect: false
			},
			radius: 0,
			tension: 1
		},
		spanGaps: true,
		data: weight
	};
};

/**
 *
 * @param {Array<WeightTrackerEntry>} entries
 * @param {Date} date
 * @param {DataViews} filter
 */
export const paintWeightTrackerEntries = (entries, date, filter) => {
	const style = getComputedStyle(document.body);
	const elemHtmlClasses = document.documentElement.classList;

	// let borderColor = style.getPropertyValue('--color-surface-200');
	// let labelColor = style.getPropertyValue('--color-surface-100');
	let labelTextColor = style.getPropertyValue('--color-surface-900');

	if (elemHtmlClasses.contains('dark')) {
		// borderColor = style.getPropertyValue('--color-surface-500');
		// labelColor = style.getPropertyValue('--color-surface-800');
		labelTextColor = style.getPropertyValue('--color-surface-100');
	}

	const noNaN = entries.map((entry) => entry.amount);

	if (noNaN.length > 0) {
		const chart = createWeightChart(filter, date, entries);
		const dataset = createWeightChartDataset(chart.data);

		return {
			chartData: {
				labels: chart.legend,
				datasets: [dataset]
			},
			chartOptions: {
				responsive: true,
				scales: {
					y: {
						suggestedMin: Math.min(...noNaN) - 2.5,
						suggestedMax: Math.max(...noNaN) + 2.5,
						ticks: {
							color: `rgb(${labelTextColor})`
						}
					},
					x: {
						ticks: {
							color: `rgb(${labelTextColor})`
						}
					}
				},
				plugins: {
					legend: {
						labels: {
							color: `rgb(${labelTextColor})`
						}
					}
				}
			}
		};
	}

	return {
		chartData: undefined,
		chartOptions: undefined
	};
};

/**
 * @param {Array<CalorieTrackerEntry>} calories
 * @param {Goal} goal
 * @return {{backgroundColor: *, data: number, label: string}[]}
 */
const createCalorieTrackerQuickviewDataset = (calories, goal) => {
	const style = getComputedStyle(document.body);
	const elemHtmlClasses = document.documentElement.classList;

	let lineColor = style.getPropertyValue('--color-surface-500');
	let borderColor = style.getPropertyValue('--color-surface-200');
	let deficitColor = style.getPropertyValue('--color-primary-500');
	let surplusColor = style.getPropertyValue('--color-warning-500');
	let maximumColor = style.getPropertyValue('--color-error-500');

	if (elemHtmlClasses.contains('dark')) {
		borderColor = style.getPropertyValue('--color-surface-500');
		lineColor = style.getPropertyValue('--color-surface-200');
	}

	const todayStr = getDateAsStr(new Date());

	calories = calories.filter((entry) => {
		return entry.added !== todayStr;
	});

	const sum = calories.reduce((acc, obj) => {
		let key = obj.added;
		if (!acc[key]) {
			acc[key] = 0;
		}
		acc[key] += obj.amount;
		return acc;
	}, {});

	const labels = [];
	const data = [];
	const lineData = [];
	const colors = [];
	const borderColors = [];

	Object.keys(sum).forEach((dateStr) => {
		let color;

		const total = sum[dateStr];
		const date = parseStringAsDate(dateStr);

		if (total <= goal.targetCalories) {
			color = deficitColor;
		} else if (total > goal.targetCalories && total <= goal.maximumCalories) {
			color = surplusColor;
		} else if (total > goal.maximumCalories) {
			color = maximumColor;
		}

		labels.push(getDateAsStr(date, display_date_format_day));
		data.push(total);
		lineData.push(goal.targetCalories);
		colors.push(`rgb(${color} / .7)`);
		borderColors.push(`rgb(${borderColor})`);
	});

	return {
		labels: labels,
		datasets: [
			{
				type: 'line',
				label: 'Target (kcal)',
				data: lineData,
				borderColor: lineColor,
				borderDash: [6],
				pointRadius: 0,
				pointHoverRadius: 10,
				spanGaps: true
			},
			{
				type: 'bar',
				label: 'Intake (kcal)',
				data: data,
				backgroundColor: colors,
				borderColor: borderColors,
				borderWidth: 2
			}
		]
	};
};

/**
 * @param {Array<CalorieTrackerEntry>} entries
 * @param {Goal} goal
 */
export const paintCalorieTrackerQuickview = (entries, goal) => {
	const style = getComputedStyle(document.body);
	const elemHtmlClasses = document.documentElement.classList;

	// let borderColor = style.getPropertyValue('--color-surface-200');
	// let labelColor = style.getPropertyValue('--color-surface-100');
	let labelTextColor = style.getPropertyValue('--color-surface-900');

	if (elemHtmlClasses.contains('dark')) {
		// borderColor = style.getPropertyValue('--color-surface-500');
		// labelColor = style.getPropertyValue('--color-surface-800');
		labelTextColor = style.getPropertyValue('--color-surface-100');
	}

	const noNaN = entries.map((entry) => entry.amount);

	const chartData = createCalorieTrackerQuickviewDataset(entries, goal);

	if (noNaN.length > 0) {
		return {
			chartData: {
				//labels: [1, 2, 3, 4, 5, 6, 7, 8],s
				labels: chartData.labels,
				datasets: chartData.datasets
			},
			chartOptions: {
				indexAxis: 'y',
				scales: {
					y: {
						beginAtZero: false
					}
				},
				responsive: true,
				aspectRatio: 0.73
			}
		};
	}

	return {
		chartData: undefined,
		chartOptions: undefined
	};
};
