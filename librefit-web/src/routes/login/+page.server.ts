/** @type {import('./$types').Actions} */
import { UserResourceApi } from 'librefit-api/rest';

export const actions = {
	default: async ({ cookies, request }) => {
		// TODO log the user in
		const data = await request.formData();

		const email = data.get('username');
		const password = data.get('password');

		const userApi = new UserResourceApi();

		return await userApi.userLoginPost(email, password);
	}
};
