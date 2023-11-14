import { api } from '$lib/server/api/index.js';
import { convertFormDataToJson, proxyFetch } from '$lib/server/api/util.js';
import { fail } from '@sveltejs/kit';

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		const userApi = api.updateUserInfo;
		const formData = await event.request.formData();

		const userData = convertFormDataToJson(formData);

		/** @type {LibreUser} */
		const user = {
			email: userData.email,
			password: userData.currentPassword,
			name: userData.username,
			avatar: userData.avatar
		};

		let result;

		const response = await proxyFetch(event.fetch, userApi, event.cookies.get('auth'), user);

		console.log(`profile statusCode=${response.status} message=${response.statusText}`);

		if (response.status === 200) {
			result = {
				success: true
			};
		} else if (response.status === 400) {
			return fail(400, 'An error occurred. Please check your input.');
		} else if (response.status === 404) {
			return fail(404, 'The password provided did not match.');
		} else {
			return fail(500, 'An error occurred. Please try again later.');
		}

		console.log(`profile returning value=${JSON.stringify(result)}`);

		return result;
	}
};
