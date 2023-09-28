import { PUBLIC_API_BASE_PATH } from '$env/static/public';
import { api } from '$lib/api';
import { redirect } from '@sveltejs/kit';
import { proxyFetch } from '$lib/api/util.js';
import { goto } from '$app/navigation';

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

		const response = await proxyFetch(event.fetch, userApi, libreUser, null);

		console.log(`login statusCode=${response.status} message=${response.statusText}`);

		if (response.status === 200) {
			throw redirect(303, '/');
		}

		return {
			error: 'Invalid username or password.'
		};
	}
};
