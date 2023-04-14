import * as dateUtil from 'date-fns';
import * as dateLocales from 'date-fns/locale'
import type {WeightTrackerEntry} from 'librefit-api/rest';

export enum DataViews {
	Today = 'TODAY',
	Week = 'WEEK',
	Month = 'MONTH',
	Year = 'YEAR'
}

export const default_date_format = 'yyyy-MM-dd'; // used for internal storage and fetch requests
export const display_date_format = 'dd.MM.yyyy'; // used for display only

export function enumKeys<O extends object, K extends keyof O = keyof O>(obj: O): K[] {
	return Object.keys(obj).filter((k) => Number.isNaN(+k)) as K[];
}

export function createWeightChart(view: DataViews, start: Date, entries: WeightTrackerEntry[]): {legend: String[], data: number[]} {
	const legend: String[] = [];
	const data: number[] = [];

	const begin = 1;
	let end: number;
	let format: String;
	let duration: dateUtil.Duration;

	switch (view) {
		case DataViews.Year: end = 12; format = 'LLLL yyyy'; duration = { months: 1}; break;
		case DataViews.Month: end = new Date(start.getFullYear(), start.getMonth(), 0).getDate();
				format = 'dd.LL'; duration = { days: 1 }; break;
		case DataViews.Week: end = 7; format = 'EEEEEE'; duration = { days: 1 }; break;
		default: end = 1; format = ''; duration = {}; break;
	}

	let tmpDate = start;
	let tmpDateStr: String = '';

	for (let i = begin, j = end - 1; i <= end; i++, j--) {
		tmpDateStr = getDateAsStr(tmpDate);

		const values = entries.filter((entry: WeightTrackerEntry) => {
			const entryAdded = parseStringAsDate(entry.added);

			let filterPassed = false;

			switch (view) {
				case DataViews.Year: filterPassed = entryAdded.getFullYear() === tmpDate.getFullYear() && entryAdded.getMonth() === tmpDate.getMonth(); break;
				case DataViews.Month:
				case DataViews.Week: filterPassed = entry.added === tmpDateStr; break;
			}

			return filterPassed;
		}).map((entry: WeightTrackerEntry) => entry.amount)

		if (values.length > 0) {
			const sum = values.reduce((a, b) => {
				return a! + b!
			});

			if (sum) {
				data[j] = sum / values.length;
			} else {
				data[j] = 0;
			}
		}

		legend[j] = dateUtil.format(tmpDate, format.valueOf(), { locale: dateLocales.enGB });

		tmpDate = dateUtil.sub(tmpDate, duration);
	}

	return {legend, data};
}

export function createWeightChartDataset(weight: number[]) {
	return {
		label: 'Weight',
		fill: true,
		lineTension: 0.3,
		backgroundColor: 'rgba(225, 204,230, .3)',
		borderColor: 'rgb(205, 130, 158)',
		borderCapStyle: 'butt',
		borderDash: [],
		borderDashOffset: 0.0,
		borderJoinStyle: 'miter',
		pointBorderColor: 'rgb(205, 130,1 58)',
		pointBackgroundColor: 'rgb(255, 255, 255)',
		pointBorderWidth: 10,
		pointHoverRadius: 5,
		pointHoverBackgroundColor: 'rgb(0, 0, 0)',
		pointHoverBorderColor: 'rgba(220, 220, 220,1)',
		pointHoverBorderWidth: 2,
		pointRadius: 1,
		pointHitRadius: 10,
		data: weight
	}
}

export function getDateAsStr(d: Date, format?: String): String {
	if (!format) {
		format = default_date_format;
	}

	return dateUtil.format(d, format.valueOf());
}

export function parseStringAsDate(str: String, format?: String) {
	if (!format) {
		format = default_date_format;
	}

	return dateUtil.parse(str.valueOf(), format.valueOf(), new Date());
}

export function convertDateStrToDisplayDateStr(str: String, format?: String): String {
	const date = parseStringAsDate(str, format);

	return getDisplayDateAsStr(date);
}

export const getDisplayDateAsStr = (d: Date, locale?: String): String => {
	/*if (!locale && navigator !== undefined) { no clue why this does not work
		locale = navigator.language;
	}*/

	if (locale) {
		return new Intl.DateTimeFormat(locale.valueOf()).format(d);
	} else {
		return dateUtil.format(d, display_date_format);
	}
}

export function weakEntityEquals(a: {id: number, added: String,  userId: number}, b: {id: number, added: String,  userId: number}) {
	return a.id === b.id && a.added === b.added && a.userId === b.userId;
}