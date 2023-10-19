import * as dateUtil from 'date-fns';
import * as dateLocales from 'date-fns/locale/index.js';

/**
 * @readonly
 * @enum {String}
 */
export const DataViews = {
	Today: 'TODAY',
	Week: 'WEEK',
	Month: 'MONTH',
	Year: 'YEAR'
};
export const default_date_format = 'yyyy-MM-dd'; // used for internal storage and fetch requests
export const display_date_format = 'dd.MM.yyyy'; // used for display only
/**
 * @param {{}} obj
 */
export function enumKeys(obj) {
	return Object.keys(obj).filter((k) => Number.isNaN(+k));
}

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
 * @param {number | Date} d
 * @param {string | undefined} [format]
 */
export function getDateAsStr(d, format) {
	if (!format) {
		format = default_date_format;
	}
	return dateUtil.format(d, format.valueOf());
}

/**
 * @param {string} str
 * @param {string | undefined} [format]
 */
export function parseStringAsDate(str, format) {
	if (!format) {
		format = default_date_format;
	}
	return dateUtil.parse(str.valueOf(), format.valueOf(), new Date());
}

/**
 * @param {string} str
 * @param {string | undefined} [format]
 */
export function convertDateStrToDisplayDateStr(str, format) {
	const date = parseStringAsDate(str, format);
	return getDisplayDateAsStr(date);
}
export const getDisplayDateAsStr = (
	/** @type {number | Date} */ d,
	/** @type {string | undefined} */ locale
) => {
	/*if (!locale && navigator !== undefined) { no clue why this does not work
        locale = navigator.language;
    }*/
	if (locale) {
		return new Intl.DateTimeFormat(locale.valueOf()).format(d);
	} else {
		return dateUtil.format(d, display_date_format);
	}
};

/**
 * @param {{ id: any; added: any; userId: any; }} a
 * @param {{ id: any; added: any; userId: any; }} b
 */
export function weakEntityEquals(a, b) {
	return a.id === b.id && a.added === b.added && a.userId === b.userId;
}
