import { Category } from '$lib/api/model.js';
import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';
import { fail } from '@sveltejs/kit';
import { DataViews } from '$lib/enum.js';
import { getDateAsStr } from '$lib/date.js';

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
			return listCaloriesForDate(newEntry.added);
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
			return listCaloriesForDate(entry.added);
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
			return listCaloriesForDate(params.date);
		} else throw response;
	});
};

export const listCaloriesForDate = (date) => {
	// add a blank entry for new input
	/** @type {CalorieTrackerEntry} */
	const blankEntry = {
		added: date,
		amount: 0,
		category: Category.Unset
	};

	return proxyFetch(fetch, api.listCalorieTrackerEntriesForDate, { date: date }).then(
		async (response) => {
			/** @type {Array<CalorieTrackerEntry>} */
			const ctList = await response.json();
			ctList.unshift(blankEntry);

			return Promise.resolve(ctList);
		}
	);
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
