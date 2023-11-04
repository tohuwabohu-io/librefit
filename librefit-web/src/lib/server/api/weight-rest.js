import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { DataViews, getDateAsStr } from '$lib/util.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const GET = async ({ fetch, url, cookies }) => {
	const listApi = api.listWeightTrackerEntriesRange;

	/** @type {Response} */
	let response = new Response();

	const fromDate = new Date();
	const toDate = new Date();
	const filter = url.searchParams.get('filter');

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

	try {
		response = await proxyFetch(fetch, listApi, cookies.get('auth'), {
			dateFrom: getDateAsStr(fromDate),
			dateTo: getDateAsStr(toDate)
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
		if (payload['weight']) {
			response = await proxyFetch(
				fetch,
				api.createWeightTrackerEntry,
				cookies.get('auth'),
				payload['weight']
			);
		} else if (payload['goal']) {
			response = await proxyFetch(fetch, api.createGoal, cookies.get('auth'), payload['weight']);
		} else {
			console.error(`weight api can't be called with payload ${JSON.stringify(payload)}`);

			response = new Response(null, {
				status: 422,
				statusText: 'Unprocessable Content'
			});
		}
	} catch (e) {
		console.error(e);
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const PUT = async ({ request, fetch, cookies }) => {
	const payload = await request.json();

	/** @type {Response} */
	let response = new Response();

	try {
		if (payload['weight']) {
			const readApi = api.readWeightTrackerEntry;
			const updateApi = api.updateWeightTrackerEntry;

			const weight = payload['weight'];

			response = await proxyFetch(fetch, readApi, cookies.get('auth'), {
				id: weight['id'],
				date: weight['date']
			}).then(async (result) => {
				/** @type {import('$lib/server/api/index.js').WeightTrackerEntry} */
				const entry = await result.json();
				entry.amount = weight['amount'];

				return proxyFetch(fetch, updateApi, cookies.get('auth'), entry);
			});
		} else if (payload['goal']) {
			const updateApi = api.updateGoal;

			response = await proxyFetch(fetch, updateApi, cookies.get('auth'), payload['goal']);
		} else {
			response = new Response(null, {
				status: 422,
				statusText: 'Unprocessable Content'
			});
		}
	} catch (e) {
		console.error(e);
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const DELETE = async ({ fetch, url, cookies }) => {
	const deleteApi = api.deleteWeightTrackerEntry;

	/** @type {Response} */
	let response = new Response();

	try {
		response = await proxyFetch(fetch, deleteApi, cookies.get('auth'), {
			id: url.searchParams.get('sequence'),
			date: url.searchParams.get('date')
		});
	} catch (e) {
		console.error(e);
	}

	return response;
};
