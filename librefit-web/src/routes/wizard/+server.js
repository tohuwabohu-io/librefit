import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export async function POST({ request, fetch }) {
	const tdee = await request.json();
	const tdeeApi = api.getTdeeCalculateAgeSexWeightHeightActivityLevelDiffGain;

	/** @type {Response} */
	let response;

	try {
		response = await proxyFetch(fetch, tdeeApi, undefined, tdee);
	} catch (e) {
		console.log(e);
		response = new Response();
	}

	return response;
}
