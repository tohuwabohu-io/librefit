import { api } from '$lib/server/api/index.js';
import { convertFormDataToJson, proxyFetch } from '$lib/server/api/util.js';
import { validateFields } from '$lib/validation.js';
import { fail } from '@sveltejs/kit';

export const registerUser = async (event) => {
	const userApi = api.postUserRegister;
	const formData = await event.request.formData();

	const user = convertFormDataToJson(formData);

	/** @type {any} */
	let result = validateFields(user);

	console.log(`register validation errors=${JSON.stringify(result)}`);

	if (!result) {
		const response = await proxyFetch(event.fetch, userApi, undefined, user);

		console.log(`register statusCode=${response.status} message=${response.statusText}`);

		if (response.status === 201) {
			result = {
				success: true
			};
		} else if (response.status === 400) {
			/** @type {ErrorResponse} */
			const errorResponse = await response.json();

			console.log(errorResponse);

			result = fail(400, errorResponse);
		} else {
			result = {
				error: 'An error occurred. Please try again later.'
			};
		}
	}

	console.log(`register returning value=${JSON.stringify(result)}`);

	return result;
};
