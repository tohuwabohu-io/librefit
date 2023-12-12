import { api } from '$lib/server/api/index.js';
import { redirect } from '@sveltejs/kit';
import { proxyFetch } from '$lib/server/api/util.js';
import { setAuthInfo } from '$lib/server/api/login.js';

const unguardedRoutes = ['/', '/login', '/register', '/tos', '/imprint'];

/** @type {import('@sveltejs/kit').Handle} */
export async function handle({ event, resolve }) {
	if (!unguardedRoutes.includes(event.url.pathname)) {
		if (!event.cookies.get('auth') || event.cookies.get('auth') === 'undefined') {
			console.log('jwt expired. refreshing...');

			const refreshToken = event.cookies.get('refresh');

			if (!refreshToken) {
				console.log('no refresh token found.');

				throw redirect(303, '/');
			} else {
				const refreshApi = api.postUserRefresh;

				await proxyFetch(event.fetch, refreshApi, null, { token: '', refreshToken: refreshToken })
					.then(async (authInfo) => setAuthInfo(await authInfo.json(), event.cookies))
					.catch((e) => {
						console.error(e);
						throw redirect(303, '/');
					});
			}
		}
	}

	return await resolve(event);
}
