import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { redirect } from '@sveltejs/kit';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	/** @type AuthInfo */
	const authInfo = {
		accessToken: cookies.get('auth'),
		refreshToken: cookies.get('refresh')
	};

	cookies.delete('auth');
	cookies.delete('refresh');

	const userApi = api.postUserLogout;
	const response = await proxyFetch(fetch, userApi, authInfo.accessToken, authInfo);

	throw redirect(303, '/');
};
