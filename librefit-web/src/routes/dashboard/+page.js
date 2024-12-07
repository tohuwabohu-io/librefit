import { getDateAsStr } from '$lib/date.ts';
import { invoke } from '@tauri-apps/api/core';

/** @type {import('./$types').PageLoad} */
export const load = async ({ fetch }) => {
	const today = new Date();

	const dashboard = invoke('daily_dashboard', { dateStr: getDateAsStr(today) });

	return {
		dashboardData: await dashboard
	};
};
