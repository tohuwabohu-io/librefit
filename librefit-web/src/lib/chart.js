import * as dateUtil from 'date-fns';
import * as dateLocales from 'date-fns/locale/index.js';
import { DataViews } from '$lib/enum.js';
import { getDateAsStr, parseStringAsDate } from '$lib/date.js';

/**
 * @param {DataViews} view
 * @param {Date} start
 * @param {any[]} entries
 */
export function createWeightChart(view, start, entries) {
	const legend = [];
	const data = [];
	const begin = 1;
	let end;
	let format;
	let duration;
	switch (view) {
		case DataViews.Year:
			end = 12;
			format = 'LLLL yyyy';
			duration = { months: 1 };
			break;
		case DataViews.Month:
			end = new Date(start.getFullYear(), start.getMonth(), 0).getDate();
			format = 'dd.LL';
			duration = { days: 1 };
			break;
		case DataViews.Week:
			end = 7;
			format = 'EEEEEE';
			duration = { days: 1 };
			break;
		default:
			end = 1;
			format = 'dd';
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
		legend[j] = dateUtil.format(tmpDate, format.valueOf(), { locale: dateLocales.enGB });
		tmpDate = dateUtil.sub(tmpDate, duration);
	}
	return { legend, data };
}

/**
 * @param {any} weight
 */
export function createWeightChartDataset(weight) {
	return {
		label: 'Weight (kg)',
		borderColor: 'rgb(14 165 233)',
		options: {
			fill: false,
			interaction: {
				intersect: false
			},
			radius: 0
		},
		spanGaps: true,
		data: weight
	};
}
const skipped = (
	/** @type {{ p0: { skip: any; }; p1: { skip: any; }; }} */ ctx,
	/** @type {any} */ value
) => (ctx.p0.skip || ctx.p1.skip ? value : undefined);
const down = (
	/** @type {{ p0: { parsed: { y: number; }; }; p1: { parsed: { y: number; }; }; }} */ ctx,
	/** @type {any} */ value
) => (ctx.p0.parsed.y >= ctx.p1.parsed.y ? value : undefined);
const up = (
	/** @type {{ p0: { parsed: { y: number; }; }; p1: { parsed: { y: number; }; }; }} */ ctx,
	/** @type {any} */ value
) => (ctx.p0.parsed.y < ctx.p1.parsed.y ? value : undefined);

/**
 *
 * @param {Array<WeightTrackerEntry>} entries
 * @param {Date} date
 * @param {DataViews} filter
 */
export const paintWeightTrackerEntries = (entries, date, filter) => {
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
						suggestedMax: Math.max(...noNaN) + 2.5
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
