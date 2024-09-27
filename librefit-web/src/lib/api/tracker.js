import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';
import { fail } from '@sveltejs/kit';
import { DataViews } from '$lib/enum.js';
import { getDateAsStr, getDaytimeFoodCategory, parseStringAsDate } from '$lib/date.js';

/**
 * @param event
 */
export const addCalories = (event) => {
	/** @type {CalorieTracker} */
	const newEntry = {
		added: event.detail.dateStr,
		amount: event.detail.value,
		category: event.detail.category
	};

	return proxyFetch(fetch, api.createCalorieTracker, newEntry).then(async (response) => {
		if (response.ok) {
			return listCaloriesForDate(parseStringAsDate(newEntry.added));
		} else throw response;
	});
};

/**
 * @param event
 */
export const updateCalories = (event) => {
	/** @type {CalorieTracker} */
	const entry = {
		sequence: event.detail.sequence,
		added: event.detail.dateStr,
		amount: event.detail.value,
		category: event.detail.category
	};

	return proxyFetch(fetch, api.updateCalorieTracker, entry).then(async (response) => {
		if (response.ok) {
			return listCaloriesForDate(parseStringAsDate(entry.added));
		} else throw response;
	});
};

/**
 * @param event
 */
export const deleteCalories = (event) => {
	const params = {
		sequence: event.detail.sequence,
		date: event.detail.dateStr
	};

	return proxyFetch(fetch, api.deleteCalorieTracker, params).then(async (response) => {
		if (response.ok) {
			return listCaloriesForDate(parseStringAsDate(params.date));
		} else throw response;
	});
};

/**
 * @param date {Date}
 * @return {Promise}
 */
export const listCaloriesForDate = (date) => {
	// add a blank entry for new input
	/** @type {CalorieTracker} */
	const blankEntry = {
		added: getDateAsStr(date),
		amount: 0,
		category: getDaytimeFoodCategory(date)
	};

	return proxyFetch(fetch, api.listCalorieTrackerForDate, { date: getDateAsStr(date) }).then(
		async (response) => {
			/** @type {Array<CalorieTracker>} */
			const ctList = await response.json();
			ctList.unshift(blankEntry);

			return Promise.resolve(ctList);
		}
	);
};

/**
 * @param dateFrom {Date}
 * @param dateTo {Date}
 * @return {Promise}
 */
export const listCalorieTrackerDatesRange = (dateFrom, dateTo) => {
	const loadCtDateApi = api.listCalorieTrackerDatesRange;

	return proxyFetch(fetch, loadCtDateApi, {
		dateFrom: getDateAsStr(dateFrom),
		dateTo: getDateAsStr(dateTo)
	});
};

/**
 * @param dateFrom {Date}
 * @param dateTo {Date}
 * @return {Promise}
 */
export const listCalorieTrackerRange = (dateFrom, dateTo) => {
	return proxyFetch(fetch, api.listCalorieTrackerRange, {
		dateFrom: getDateAsStr(dateFrom),
		dateTo: getDateAsStr(dateTo)
	});
};

/**
 * @param filter {DataViews}
 */
export const listCaloriesFiltered = (filter) => {
	const fromDate = new Date();
	const toDate = new Date();

	switch (filter) {
		case DataViews.Week:
			fromDate.setDate(fromDate.getDate() - 7);
			break;
		case DataViews.Month:
			fromDate.setMonth(fromDate.getMonth() - 1);
			break;
		case DataViews.Year:
			fromDate.setFullYear(fromDate.getFullYear() - 1);
			break;
		default:
			break;
	}

	return proxyFetch(fetch, api.listCalorieTrackerRange, {
		dateFrom: getDateAsStr(fromDate),
		dateTo: getDateAsStr(toDate)
	});
};

/**
 * @param event
 */
export const addWeight = (event) => {
	/** @type {WeightTracker} */
	const newEntry = {
		sequence: event.detail.sequence,
		added: event.detail.dateStr,
		amount: event.detail.value
	};

	return proxyFetch(fetch, api.createWeightTracker, newEntry).then(async (response) => {
		if (response.ok) {
			return listWeightForDate(parseStringAsDate(newEntry.added));
		} else throw response;
	});
};

/**
 * @param event
 */
export const updateWeight = (event) => {
	/** @type WeightTracker */
	const entry = {
		sequence: event.detail.sequence,
		added: event.detail.dateStr,
		amount: event.detail.value
	};

	return proxyFetch(fetch, api.updateWeightTracker, entry).then(async (response) => {
		if (response.ok) {
			return listWeightForDate(parseStringAsDate(entry.added));
		} else throw response;
	});
};

/**
 * @param event
 */
export const deleteWeight = (event) => {
	const params = {
		sequence: event.detail.sequence,
		date: event.detail.dateStr
	};

	return proxyFetch(fetch, api.deleteWeightTracker, params).then(async (response) => {
		if (response.ok) {
			return listWeightForDate(parseStringAsDate(params.date));
		} else throw response;
	});
};

/**
 * @param filter {DataViews}
 */
export const listWeightFiltered = (filter) => {
	const fromDate = new Date();
	const toDate = new Date();

	switch (filter) {
		case DataViews.Week:
			fromDate.setDate(fromDate.getDate() - 7);
			break;
		case DataViews.Month:
			fromDate.setMonth(fromDate.getMonth() - 1);
			break;
		case DataViews.Year:
			fromDate.setFullYear(fromDate.getFullYear() - 1);
			break;
		default:
			break;
	}

	return proxyFetch(fetch, api.listWeightTrackerRange, {
		dateFrom: getDateAsStr(fromDate),
		dateTo: getDateAsStr(toDate)
	});
};

/**
 * @param dateFrom {Date}
 * @param dateTo {Date}
 * @return {Promise}
 */
export const listWeightTrackerDatesRange = (dateFrom, dateTo) => {
	return proxyFetch(fetch, api.listWeightTrackerDatesRange, {
		dateFrom: getDateAsStr(dateFrom),
		dateTo: getDateAsStr(dateTo)
	});
};

/**
 *
 * @param dateFrom {Date}
 * @param dateTo {Date}
 * @return {Promise}
 */
export const listWeightRange = (dateFrom, dateTo) => {
	return proxyFetch(fetch, api.listWeightTrackerRange, {
		dateFrom: getDateAsStr(dateFrom),
		dateTo: getDateAsStr(dateTo)
	});
};

/**
 * @param date {Date}
 * @return {Promise}
 */
export const listWeightForDate = (date) => {
	return proxyFetch(fetch, api.listWeightTracker, {
		date: getDateAsStr(date)
	});
};

/**
 * @param formData {FormData}
 * @return {Promise}
 */
export const startImport = async (formData) => {
	const importApi = api.postImportBulk;

	const importerSelection = formData['importer'];

	/** @type File */
	const file = formData.get('file');

	if (file.type !== 'text/csv') {
		return fail(400, { errors: [{ field: 'file', message: 'Please choose a valid CSV file.' }] });
	}

	if (file.size > 32768) {
		return fail(400, {
			errors: [{ field: 'file', message: 'File size exceeds the limit of 32 KB.' }]
		});
	}

	/** @type ImportConfig */
	const config = {
		datePattern: formData['datePattern'],
		headerLength: formData['headerLength'],
		drop: formData['drop'],
		updateCalorieTracker: importerSelection !== 'W',
		updateWeightTracker: importerSelection !== 'C'
	};

	formData.append('config', JSON.stringify(config));

	formData.delete('datePattern');
	formData.delete('headerLength');
	formData.delete('drop');
	formData.delete('importer');

	const response = await proxyFetch(fetch, importApi, formData);

	let result;

	if (response.status === 200) {
		result = {
			success: true
		};
	} else {
		/** @type ErrorResponse */
		const errorResponse = await response.json();

		result = fail(response.status, errorResponse);
	}

	return result;
};
