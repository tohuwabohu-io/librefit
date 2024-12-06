import { subDays } from 'date-fns';
import { invoke } from '@tauri-apps/api/core';
import { listCalorieTrackerDatesRange } from '$lib/api/tracker.js';

export const load = async ({ fetch }) => {
	const today = getDateAsStr(new Date());
	const fromDate = getDateAsStr(subDays(today, 6));
	
	/** @type Array<String> */
	const trackedDaysWeek = await listCalorieTrackerDatesRange(fromDate, today);
	
	/** @type Array<CalorieTracker> */
	const calorieTrackerToday = await invoke('get_calorie_tracker_for_date_range', { 
		dateFromStr: fromDate, 
		dateToStr: today 
	});

	if (calorieTrackerToday && trackedDaysWeek) {
		return {
			availableDates: trackedDaysWeek,
			entryToday: calorieTrackerToday
		};
	} else {
		return { error: 'An error has occurred. Please try again later.' };
	}
};
