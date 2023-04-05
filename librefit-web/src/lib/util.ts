import * as dateUtil from 'date-fns';

export enum DataViews {
	Today = 'TODAY',
	Week = 'WEEK',
	Month = 'MONTH',
	Year = 'YEAR'
}

export function enumKeys<O extends object, K extends keyof O = keyof O>(obj: O): K[] {
	return Object.keys(obj).filter((k) => Number.isNaN(+k)) as K[];
}

export function getDateAsStr(d: Date, format?: string): string {
	if (!format) {
		format = 'yyyy-MM-dd';
	}

	return dateUtil.format(d, format);
}