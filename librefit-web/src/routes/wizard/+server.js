import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { fail } from '@sveltejs/kit';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export async function POST({ request, fetch }) {
	const tdee = await request.json();
	const tdeeApi = api.calculateTdee;

	/** @type {Response} */
	return await proxyFetch(fetch, tdeeApi, undefined, tdee);
}
