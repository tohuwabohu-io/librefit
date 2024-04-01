import * as dateUtil from 'date-fns';

export const default_date_format = 'yyyy-MM-dd'; // used for internal storage and fetch requests
export const display_date_format = 'dd.MM.yyyy'; // used for display only
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
 * @param {String} str
 * @param {String | undefined} [format]
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
 * @readonly
 * @enum {string}
 */
export const Daytime = {
	Morning: 'morning',
	Day: 'day',
	Afternoon: 'afternoon',
	Evening: 'evening',
	Night: 'night'
};
/**
 * @param date {Date}
 */
export const getDaytimeGreeting = (date) => {
	const hours = dateUtil.getHours(date);

	if (hours >= 0 && hours <= 4) {
		return Daytime.Night;
	} else if (hours >= 5 && hours <= 11) {
		return Daytime.Morning;
	} else if (hours >= 12 && hours <= 14) {
		return Daytime.Day;
	} else if (hours >= 15 && hours <= 18) {
		return Daytime.Afternoon;
	} else if (hours >= 19 && hours <= 24) {
		return Daytime.Night;
	} else {
		return Daytime.Day;
	}
};

/** @param date {Date} */
export const getDaytimeFoodCategory = (date) => {
	const hours = dateUtil.getHours(date);

	if (hours >= 0 && hours <= 4) {
		return 's';
	} else if (hours >= 5 && hours <= 11) {
		return 'b';
	} else if (hours >= 12 && hours <= 15) {
		return 'l';
	} else if ((hours) => 16 && hours <= 20) {
		return 'd';
	} else {
		return 't';
	}
};
