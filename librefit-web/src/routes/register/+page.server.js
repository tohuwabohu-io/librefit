import { api } from '$lib/api';
import { convertFormDataToJson, proxyFetch } from '$lib/api/util.js';
import { validateFields } from '$lib/validation.js';

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		const userApi = api.postUserRegister;
		const formData = await event.request.formData();

		const user = convertFormDataToJson(formData);

		/** @type {any} */
		let result = validateFields(user);

		console.log(`register validation errors=${JSON.stringify(result)}`);

		if (!result) {
			const response = await proxyFetch(event.fetch, userApi, user, null);

			console.log(`register statusCode=${response.status} message=${response.statusText}`);

			if (response.status === 201) {
				result = {
					success: true
				};
			} else if (response.status === 400) {
				console.log(await response.json());
			} else {
				result = {
					error: 'An error occurred. Please try again later.'
				};
			}
		}

		console.log(`register returning value=${JSON.stringify(result)}`);

		return result;
	}
};
