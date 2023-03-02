import { Configuration, UserResourceApi } from 'librefit-api/rest';

export const actions = {
	default: async ({ request }) => {
		const data = await request.formData();

		const userApi = new UserResourceApi(
			new Configuration({
				basePath: import.meta.env.VITE_API_BASE_PATH
			})
		);

		const username = data.get('username');
		const email = data.get('email');
		const password = data.get('password');

		return await userApi
			.userRegisterPost({
				name: username,
				email: email,
				password: password
			})
			.catch((error) => console.error(error));
	}
};
