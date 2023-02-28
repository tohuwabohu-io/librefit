import {UserResourceApi} from 'librefit-api/rest';

export const actions = {
    default: async ({request}) => {
        const result = {
            status: 500
        }

        const data = await request.formData();

        const userApi = new UserResourceApi(undefined, 'http://127.0.0.1:8080');

        const username = data.get('username');
        const email = data.get('email');
        const password = data.get('password');

        try {
            const response = await userApi.userRegisterPost(username, email, password);

            result.status = response.status;
        } catch (e) {
            console.error(e)
        }

        return {
            success: result.status === 200
        };
    }
}