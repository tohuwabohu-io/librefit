import { proxyFetch } from '$lib/server/api/util.js';
import { api } from '$lib/server/api/index.js';

/** @type {import('./$types').LayoutServerLoad} */
export async function load({ fetch, cookies }) {
	const jwt = cookies.get('auth');
	const authenticated = jwt !== undefined && jwt !== 'undefined';

	if (authenticated) {
		const userApi = api.readUserInfo;

		const user = await proxyFetch(fetch, userApi, jwt);

		return {
			authenticated: true,
			userData: await user.json()
		};
	}

	return {
		authenticated: false
	};
}
