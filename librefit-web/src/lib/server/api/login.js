import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { redirect, fail } from '@sveltejs/kit';
import * as dateUtil from 'date-fns';

export const login = async (event) => {
	const userApi = api.postUserLogin;

	const formData = await event.request.formData();

	/** @type import('$lib/server/api/index.js').LibreUser */
	const libreUser = {
		email: String(formData.get('email')),
		password: String(formData.get('password'))
	};

	const response = await proxyFetch(event.fetch, userApi, undefined, libreUser);

	/** @type ErrorResponse */
	let error = {
		errors: [
			{
				field: 'email',
				message: 'An error occurred. Please try again later.'
			}
		]
	};

	if (response.status === 200) {
		/** @type {AuthInfo} */
		const auth = await response.json();

		setAuthInfo(auth, event.cookies);

		throw redirect(303, '/dashboard');
	} else if (response.status === 404) {
		error.errors[0].message = 'User not found.';

		return fail(404, error);
	}

	return fail(500, error);
};

/**
 * @param {AuthInfo} authInfo
 * @param {Cookies} cookies
 */
export const setAuthInfo = (authInfo, cookies) => {
	console.log('updated authInfo.');

	cookies.set('auth', authInfo.accessToken, {
		httpOnly: true,
		path: '/',
		secure: true,
		sameSite: 'strict',
		expires: dateUtil.parse(authInfo.accessExpires, "yyyy-MM-dd'T'HH:mm:ss", new Date())
	});

	cookies.set('refresh', authInfo.refreshToken, {
		httpOnly: true,
		path: '/',
		secure: true,
		sameSite: 'strict',
		expires: dateUtil.parse(authInfo.refreshExpires, "yyyy-MM-dd'T'HH:mm:ss", new Date())
	});
};
