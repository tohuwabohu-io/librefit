import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import * as weight_crud from '$lib/server/api/weight-rest.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export async function POST({ request, fetch, cookies }) {
	const peek = request.clone();
	const payload = await peek.json();

	/** @type Response */
	let response;

	if (payload.tdee) {
		const tdee = await request.json();
		const tdeeApi = api.calculateTdee;

		/** @type {Response} */
		response = await proxyFetch(fetch, tdeeApi, undefined, tdee);
	} else if (payload.goal) {
		response = weight_crud.POST({ request, fetch, cookies });
	} else {
		console.error(`wizard api can't be called with payload ${JSON.stringify(payload)}`);

		response = new Response(null, {
			status: 422,
			statusText: 'Unprocessable Content'
		});
	}

	return response;
}
