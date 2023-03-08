/** @type {import('./$types').Actions} */
import { UserResourceApi } from 'librefit-api/rest';
import { Configuration } from 'librefit-api/rest';
import { PUBLIC_API_BASE_PATH } from '$env/static/public';

export const actions = {
	default: async ({ cookies, request }) => {
		// TODO log the user in
		const data = await request.formData();

		const email = data.get('username');
		const password = data.get('password');

		const userApi = new UserResourceApi(
			new Configuration({
				basePath: PUBLIC_API_BASE_PATH
			})
		);

		return await userApi.userLoginPost({
			email,
			password
		});
	}
};
