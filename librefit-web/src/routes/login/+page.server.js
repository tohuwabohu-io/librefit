import { api } from '$lib/api';
import { redirect } from '@sveltejs/kit';
import { proxyFetch } from '$lib/api/util.js';

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		const userApi = api.postUserLogin;

		const formData = await event.request.formData();

		/** @type import('$lib/api').LibreUser */
		const libreUser = {
			email: String(formData.get('email')),
			password: String(formData.get('password'))
		};

		const response = await proxyFetch(event.fetch, userApi, undefined, libreUser);

		console.log(`login statusCode=${response.status} message=${response.statusText}`);

		console.log(response.headers);

		if (response.status === 200) {
			/** @type {import('$lib/api').AuthenticationResponse} */
			const auth = await response.json();

			event.cookies.set('auth', auth.token, {
				httpOnly: true,
				path: '/',
				secure: true,
				sameSite: 'strict',
				maxAge: 60 * 60 * 24 // 1 day
			});

			throw redirect(303, '/');
		}

		return {
			error: 'Invalid username or password.'
		};
	}
};
