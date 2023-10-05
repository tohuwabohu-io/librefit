import { api } from '$lib/api';
import { convertFormDataToJson, proxyFetch } from '$lib/api/util.js';

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		const userApi = api.postUserRegister;
		const formData = await event.request.formData();

		/** @type {any} */
		let result = validate(formData);

		if (!result) {
			const user = convertFormDataToJson(formData);

			const response = await proxyFetch(event.fetch, userApi, user, null);

			console.log(response);

			if (response.status === 200) {
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

		return result;
	}
};

/** @param {FormData} formData */
const validate = (formData) => {
	const errors = {};

	const email = String(formData.get('email'));
	const pwd = String(formData.get('password'));
	const pwdConfirmation = String(formData.get('passwordConfirmation'));
	const tosAccepted = formData.get('confirmation');

	if (email === null || (email.indexOf('@') <= 0 && email.length <= 4)) {
		errors['email'] = 'Please enter a valid email address.';
	}

	if (pwd === null || pwd.indexOf('a') < 0) {
		errors['password'] = "Chosen password must contain at least one 'a' letter.";
	}

	if (pwdConfirmation === null || pwd !== pwdConfirmation) {
		errors['passwordConfirmation'] = 'Passwords do not match.';
	}

	if (Boolean(tosAccepted) !== true || tosAccepted === undefined) {
		errors['confirmation'] = 'Please accept our terms and conditions.';
	}

	if (Object.keys(errors).length > 0) {
		return {
			errors: errors
		};
	}

	return null;
};
