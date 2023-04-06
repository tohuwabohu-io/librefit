import * as dateUtil from 'date-fns';

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

export function getDateAsStr(d: Date, format?: string): string {
	if (!format) {
		format = default_date_format;
	}

	return dateUtil.format(d, format);
}

export function parseStringAsDate(str: string, format?: string) {
	if (!format) {
		format = default_date_format;
	}

	return dateUtil.parse(str, format, new Date());
}

export function convertDateStrToDisplayDateStr(str: string, format?: string): string {
	const date = parseStringAsDate(str, format);

	return getDisplayDateAsStr(date);
}

export const getDisplayDateAsStr = (d: Date, locale?: string): string => {
	/*if (!locale && navigator !== undefined) { no clue why this does not work
		locale = navigator.language;
	}*/

	if (locale) {
		return new Intl.DateTimeFormat(locale).format(d);
	} else {
		return dateUtil.format(d, display_date_format);
	}
}
