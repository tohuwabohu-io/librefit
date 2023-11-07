import { api } from '$lib/server/api/index.js';
import { convertFormDataToJson, proxyFetch } from '$lib/server/api/util.js';
import { validateFields, validatePassword, validatePasswordConfirmation } from '$lib/validation.js';

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

		const pwdError = validatePassword(userData.currentPassword);
		const pwdConfirmationError = null;

		/*const pwdError = validatePassword(userData.password);
		const pwdConfirmationError = validatePasswordConfirmation(
			userData.password,
			userData.passwordConfirmation
		);*/

		console.log(user);

		let result;

		const response = await proxyFetch(event.fetch, userApi, event.cookies.get('auth'), user);

		console.log(`profile statusCode=${response.status} message=${response.statusText}`);

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

		console.log(`register returning value=${JSON.stringify(result)}`);

		return result;
	}
};
