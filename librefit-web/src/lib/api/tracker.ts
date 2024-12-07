import { DataViews } from '../enum';
import { getDateAsStr, getDaytimeFoodCategory } from '../date';
import { invoke } from '@tauri-apps/api/core';
import { CalorieTracker, NewCalorieTracker, NewWeightTracker, WeightTracker } from '../model';

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

export const deleteCalories = (event) => {
	return invoke('delete_calorie_tracker_entry', {
		trackerId: event.detail.id,
		addedStr: event.detail.dateStr
	});
};

export const listCaloriesForDate = (dateStr: string): Promise<Array<CalorieTracker | NewCalorieTracker>> => {
	// add a blank entry for new input
	const blankEntry: NewCalorieTracker = {
		added: dateStr,
		amount: 0,
		category: getDaytimeFoodCategory(dateStr),
		description: ''
	};

	return listCalorieTrackerRange(dateStr, dateStr).then(
		async (response) => {
			const ctList: Array<CalorieTracker | NewCalorieTracker> = response;
			ctList.unshift(blankEntry);

			return Promise.resolve(ctList);
		}
	);
};

export const listCalorieTrackerDatesRange = (dateFrom: string, dateTo: string): Promise<Array<CalorieTracker>> => {
	return invoke('get_calorie_tracker_dates_in_range', {
		dateFromStr: dateFrom,
		dateToStr: dateTo
	});
};

export const listCalorieTrackerRange = (dateFrom: string, dateTo: string): Promise<Array<CalorieTracker | NewCalorieTracker>> => {
	return invoke('get_calorie_tracker_for_date_range', {
		dateFromStr: dateFrom,
		dateToStr: dateTo
	});
};

export const listCaloriesFiltered = (filter: DataViews) => {
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
export const addWeight = (event): Promise<WeightTracker> => {
	const newEntry: NewWeightTracker = {
		added: event.detail.dateStr,
		amount: event.detail.value
	};

	return invoke('create_weight_tracker_entry', { newEntry });
};

/**
 * @param event
 */
export const updateWeight = (event): Promise<WeightTracker> => {
	/** @type NewWeightTracker */
	const entry: NewWeightTracker = {
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
export const deleteWeight = (event): Promise<WeightTracker> => {
	return invoke('delete_weight_tracker_entry', {
		trackerId: event.detail.id,
		addedStr: event.detail.dateStr
	});
};

export const listWeightFiltered = (filter: DataViews): Promise<Array<WeightTracker>> => {
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

export const listWeightRange = (dateFrom: string, dateTo: string): Promise<WeightTracker> => {
	return invoke('get_weight_tracker_for_date_range', {
		dateFromStr: dateFrom,
		dateToStr: dateTo
	});
};

