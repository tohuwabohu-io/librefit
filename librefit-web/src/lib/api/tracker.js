import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';
import { fail } from '@sveltejs/kit';
import { DataViews } from '$lib/enum.js';
import { getDateAsStr, getDaytimeFoodCategory, parseStringAsDate } from '$lib/date.js';

/**
 * @param event
 */
export const addCalories = (event) => {
	/** @type {CalorieTrackerEntry} */
	const newEntry = {
		added: event.detail.date,
		amount: event.detail.value,
		category: event.detail.category
	};

	return proxyFetch(fetch, api.createCalorieTrackerEntry, newEntry).then(async (response) => {
		if (response.ok) {
			return listCaloriesForDate(parseStringAsDate(newEntry.added));
		} else throw response;
	});
};

/**
 * @param event
 */
export const updateCalories = (event) => {
	/** @type {CalorieTrackerEntry} */
	const entry = {
		sequence: event.detail.sequence,
		added: event.detail.date,
		amount: event.detail.value,
		category: event.detail.category
	};

	return proxyFetch(fetch, api.updateCalorieTrackerEntry, entry).then(async (response) => {
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
		date: event.detail.date
	};

	return proxyFetch(fetch, api.deleteCalorieTrackerEntry, params).then(async (response) => {
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
	/** @type {CalorieTrackerEntry} */
	const blankEntry = {
		added: date,
		amount: 0,
		category: getDaytimeFoodCategory(date)
	};

	return proxyFetch(fetch, api.listCalorieTrackerEntriesForDate, { date: getDateAsStr(date) }).then(
		async (response) => {
			/** @type {Array<CalorieTrackerEntry>} */
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
export const listCalorieTrackerEntriesRange = (dateFrom, dateTo) => {
	return proxyFetch(fetch, api.listCalorieTrackerEntriesRange, {
		dateFrom: getDateAsStr(dateFrom),
		dateTo: getDateAsStr(dateTo)
	});
};

/**
 * @param event
 */
export const addWeight = (event) => {
	/** @type {WeightTrackerEntry} */
	const newEntry = {
		sequence: event.detail.sequence,
		added: event.detail.todayDateStr,
		amount: event.detail.value
	};

	return proxyFetch(fetch, api.createWeightTrackerEntry, newEntry).then(async (response) => {
		if (response.ok) {
			return proxyFetch(fetch, api.findLastWeightTrackerEntry);
		} else throw response;
	});
};

/**
 * @param event
 */
export const updateWeight = (event) => {
	const entry = {
		sequence: event.detail.sequence,
		date: event.detail.date,
		amount: event.detail.value
	};

	return proxyFetch(fetch, api.updateWeightTrackerEntry, entry).then(async (response) => {
		if (response.ok) {
			return proxyFetch(fetch, api.listWeightTrackerEntries, { date: entry.added });
		} else throw response;
	});
};

/**
 * @param event
 */
export const deleteWeight = (event) => {
	const params = {
		sequence: event.detail.sequence,
		date: event.detail.date
	};

	return proxyFetch(fetch, api.deleteWeightTrackerEntry, params).then(async (response) => {
		if (response.ok) {
			return proxyFetch(fetch, api.listWeightTrackerEntries, { date: params.date });
		} else throw response;
	});
};

/**
 * @param filter {DataView}
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

	return proxyFetch(fetch, api.listWeightTrackerEntriesRange, {
		dateFrom: getDateAsStr(fromDate),
		dateTo: getDateAsStr(toDate)
	});
};

/**
 *
 * @param dateFrom {Date}
 * @param dateTo {Date}
 * @return {Promise}
 */
export const listWeightRange = (dateFrom, dateTo) => {
	return proxyFetch(fetch, api.listWeightTrackerEntriesRange, {
		dateFrom: getDateAsStr(dateFrom),
		dateTo: getDateAsStr(dateTo)
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
