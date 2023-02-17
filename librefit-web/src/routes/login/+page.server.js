/** @type {import('./$types').Actions} */

import { UserResourceApi } from "librefit-api/rest/api";

export const actions = {
    default: async ({ cookies, request }) => {
        let status = 404;

        // TODO log the user in
        const data = await request.formData();

        const email = data.get('username');
        const password = data.get('password');

        const userApi = new UserResourceApi({
            basePath: 'http://127.0.0.1:8080',
        } );

        try {
            const response = await userApi.userLoginPost(email, password);
            status = response.status;
        } catch(e) {
            console.log(e.statusCode);
            console.log(e.message);
        }

        return {
            success: status === 200
        }
    }
};