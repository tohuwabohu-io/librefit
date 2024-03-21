import { api } from '$lib/api/index.js';
import { proxyFetch } from '$lib/api/util.js';
import { redirect } from '@sveltejs/kit';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch }) => {
	const userApi = api.postUserLogout;

	await proxyFetch(fetch, userApi);

	throw redirect(303, '/');
};
