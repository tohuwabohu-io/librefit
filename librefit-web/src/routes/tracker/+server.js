import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { Category } from '$lib/api/model.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const GET = async ({ fetch, url, cookies }) => {
	const listApi = api.listCalorieTrackerEntriesForDate;

	/** @type {Response} */
	let response = new Response();

	const date = url.searchParams.get('added');

	// add a blank entry for new input
	/** @type {CalorieTrackerEntry} */
	const blankEntry = {
		added: date,
		amount: 0,
		category: Category.Unset
	};

	try {
		response = await proxyFetch(fetch, listApi, cookies.get('auth'), {
			date: date
		}).then(async (result) => {
			/** @type {Array<CalorieTrackerEntry>} */
			const entries = await result.json();
			entries.unshift(blankEntry);

			return new Response(JSON.stringify(entries));
		});
	} catch (e) {
		console.error(e);
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const POST = async ({ request, fetch, cookies }) => {
	const payload = await request.json();

	/** @type {Response} */
	let response = new Response();

	try {
		response = await proxyFetch(fetch, api.createCalorieTrackerEntry, cookies.get('auth'), payload);
	} catch (e) {
		console.error(e);
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const PUT = async ({ request, fetch, cookies }) => {
	/** @type {CalorieTrackerEntry} */
	const params = await request.json();

	/** @type {Response} */
	let response = new Response();

	try {
		response = await proxyFetch(fetch, api.readCalorieTrackerEntry, cookies.get('auth'), {
			id: params.id,
			date: params.added
		}).then(async (result) => {
			/** @type {CalorieTrackerEntry} */
			const entry = await result.json();
			entry.amount = params.amount;
			entry.category = params.category;

			return proxyFetch(fetch, api.updateCalorieTrackerEntry, cookies.get('auth'), entry);
		});
	} catch (e) {
		console.error(e);
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const DELETE = async ({ fetch, url, cookies }) => {
	/** @type {Response} */
	let response = new Response();

	try {
		response = await proxyFetch(fetch, api.deleteCalorieTrackerEntry, cookies.get('auth'), {
			id: url.searchParams.get('sequence'),
			date: url.searchParams.get('added')
		});
	} catch (e) {
		console.error(e);
	}

	return response;
};
