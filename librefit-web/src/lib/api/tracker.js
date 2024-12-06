import { DataViews } from '$lib/enum.js';
import { getDateAsStr, getDaytimeFoodCategory } from '$lib/date.js';
import { invoke } from '@tauri-apps/api/core';

/**
 * @param event
 */
export const addCalories = (event) => {
	/** @type {NewCalorieTracker} */
	const newEntry = {
		added: event.detail.dateStr,
		amount: event.detail.value,
		category: event.detail.category,
		description: ''
	};

	return invoke('create_calorie_tracker_entry', { newEntry });
};

/**
 * @param event
 */
export const updateCalories = (event) => {
	/** @type {NewCalorieTracker} */
	const entry = {
		added: event.detail.dateStr,
		amount: event.detail.value,
		category: event.detail.category,
		description: ''
	};

	return invoke('update_calorie_tracker_entry', {
		trackerId: event.detail.id,
		updatedEntry: entry
	});
};

/**
 * @param event
 */
export const deleteCalories = (event) => {
	return invoke('delete_calorie_tracker_entry', {
		trackerId: event.detail.id,
		addedStr: event.detail.dateStr
	});
};

/**
 * @param dateStr {String}
 * @return {Promise}
 */
export const listCaloriesForDate = (dateStr) => {
	// add a blank entry for new input
	/** @type {CalorieTracker} */
	const blankEntry = {
		added: dateStr,
		amount: 0,
		category: getDaytimeFoodCategory(dateStr)
	};

	return listCalorieTrackerRange(dateStr, dateStr).then(
		async (response) => {
			/** @type {Array<CalorieTracker>} */
			const ctList = await response;
			ctList.unshift(blankEntry);

			return Promise.resolve(ctList);
		}
	);
};

/**
 * @param dateFrom {String}
 * @param dateTo {String}
 * @return {Promise}
 */
export const listCalorieTrackerDatesRange = (dateFrom, dateTo) => {
	return invoke('get_calorie_tracker_dates_in_range', {
		dateFromStr: dateFrom,
		dateToStr: dateTo
	});
};

/**
 * @param dateFrom {String}
 * @param dateTo {String}
 * @return {Promise}
 */
export const listCalorieTrackerRange = (dateFrom, dateTo) => {
	return invoke('get_calorie_tracker_for_date_range', {
		dateFromStr: dateFrom,
		dateToStr: dateTo
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

	return listCalorieTrackerRange(getDateAsStr(fromDate), getDateAsStr(toDate));
};

/**
 * @param event
 */
export const addWeight = (event) => {
	/** @type {NewWeightTracker} */
	const newEntry = {
		id: event.detail.id,
		added: event.detail.dateStr,
		amount: event.detail.value
	};

	return invoke('create_weight_tracker_entry', { newEntry });
};

/**
 * @param event
 */
export const updateWeight = (event) => {
	/** @type NewWeightTracker */
	const entry = {
		added: event.detail.dateStr,
		amount: event.detail.value
	};

	return invoke('update_weight_tracker_entry', {
		trackerId: event.detail.id,
		updatedEntry: entry
	});
};

/**
 * @param event
 */
export const deleteWeight = (event) => {
	return invoke('delete_weight_tracker_entry', {
		trackerId: event.detail.id,
		addedStr: event.detail.dateStr
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

	return invoke('get_weight_tracker_for_date_range', {
		dateFromStr: getDateAsStr(fromDate),
		dateToStr: getDateAsStr(toDate)
	});
};

/**
 *
 * @param dateFrom {String}
 * @param dateTo {String}
 * @return {Promise}
 */
export const listWeightRange = (dateFrom, dateTo) => {
	return invoke('get_weight_tracker_for_date_range',{
		dateFromStr: dateFrom,
		dateToStr: dateTo
	});
};

