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

	cookies.delete('auth', {
		path: '/'
	});

	cookies.delete('refresh', {
		path: '/'
	});

	const userApi = api.postUserLogout;

	await proxyFetch(fetch, userApi, authInfo.accessToken, authInfo);

	throw redirect(303, '/');
};
