import { api } from '$lib/api/index.js';
import { convertFormDataToJson, proxyFetch } from '$lib/api/util.js';
import { fail } from '@sveltejs/kit';
import { validateFields } from '$lib/validation.js';

/**
 * @param formData {FormData}
 * @return {Promise}
 */
export const updateProfile = async (formData) => {
	const updateApi = api.updateUserInfo;
	const userApi = api.readUserInfo;

	const userData = convertFormDataToJson(formData);

	/** @type {LibreUser} */
	const user = {
		email: userData.email,
		password: userData.currentPassword,
		name: userData.username,
		avatar: userData.avatar
	};

	const response = await proxyFetch(fetch, updateApi, user);

	if (response.status === 200) {
		return proxyFetch(fetch, userApi);
	} else if (response.status === 400) {
		return fail(400, await response.json());
	} else if (response.status === 404) {
		/** @type {ErrorResponse} */
		return fail(404, {
			errors: [{ field: 'password', message: 'The password provided did not match.' }]
		});
	} else {
		return fail(response.statusCode, { error: 'An error occurred. Please try again later.' });
	}
};
/**
 * @param formData {FormData}
 * @return {Promise}
 */
export const login = async (formData) => {
	const loginApi = api.postUserLogin;
	const userApi = api.readUserInfo;

	const response = await proxyFetch(fetch, loginApi, formData);

	if (response.status === 200) {
		return proxyFetch(fetch, userApi);
	} else if (response.status === 404) {
		return fail(404, { errors: [{ field: 'email', message: 'User not found.' }] });
	}

	return fail(response.status, {
		errors: [{ field: 'email', message: 'An error occurred. Please try again later.' }]
	});
};
/**
 * @param formData {FormData}
 * @return {Promise<*>}
 */
export const registerUser = async (formData) => {
	const userApi = api.postUserRegister;

	const user = convertFormDataToJson(formData);

	/** @type {any} */
	let result = validateFields(user);

	if (!result) {
		const response = await proxyFetch(fetch, userApi, user);

		if (response.status === 201) {
			result = { success: true };
		} else if (response.status === 400) {
			/** @type {ErrorResponse} */
			return fail(400, await response.json());
		} else {
			return fail(response.statusCode, {
				error: 'An error occurred. Please try again later.'
			});
		}
	}

	return result;
};
