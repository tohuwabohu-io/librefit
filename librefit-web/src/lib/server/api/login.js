import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { redirect } from '@sveltejs/kit';

export const login = async (event) => {
	const userApi = api.postUserLogin;

	const formData = await event.request.formData();

	/** @type import('$lib/server/api/index.js').LibreUser */
	const libreUser = {
		email: String(formData.get('email')),
		password: String(formData.get('password'))
	};

	const response = await proxyFetch(event.fetch, userApi, undefined, libreUser);

	if (response.status === 200) {
		/** @type {import('$lib/server/api/index.js').AuthenticationResponse} */
		const auth = await response.json();

		event.cookies.set('auth', auth.token, {
			httpOnly: true,
			path: '/',
			secure: true,
			sameSite: 'strict',
			maxAge: 1000 * 60 * 15 // 15 mins
		});

		throw redirect(303, '/dashboard');
	}

	return {
		error: 'Invalid username or password.'
	};
};
