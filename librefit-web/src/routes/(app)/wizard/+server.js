import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import * as weight_crud from '$lib/server/api/weight-rest.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export async function POST({ request, fetch, cookies }) {
	const peek = request.clone();
	const peeked = await peek.json();

	/** @type Response */
	let response;

	if (peeked.tdee) {
		const payload = await request.json();

		/** @type {Response} */
		response = await proxyFetch(fetch, api.calculateTdee, undefined, payload.tdee).then(
			async (response) => {
				const calculationResult = await response.json();

				return new Response(JSON.stringify(calculationResult), {
					status: response.status,
					statusText: response.statusText
				});
			}
		);
	} else if (peeked.goal || peeked.weight) {
		response = weight_crud.POST({ request, fetch, cookies });
	} else {
		console.error(`wizard api can't be called with payload ${JSON.stringify(peeked)}`);

		response = new Response(null, {
			status: 422,
			statusText: 'Unprocessable Content'
		});
	}

	return response;
}
