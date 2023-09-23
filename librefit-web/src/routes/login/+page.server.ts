import {UserResourceApi} from 'librefit-api/rest';
import type { AuthenticationResponse } from 'librefit-api/rest';
import {AUTH_TOKEN_KEY, DEFAULT_CONFIG} from '$lib/api/Config';
import type { Cookies } from '@sveltejs/kit';
import {fail, redirect} from '@sveltejs/kit';
import type {UserLoginPostRequest} from 'librefit-api/rest/apis/UserResourceApi';

const api = new UserResourceApi(DEFAULT_CONFIG);

const createSession = (cookies: Cookies, response: AuthenticationResponse) => {
    cookies.set(AUTH_TOKEN_KEY, response.token, {
        path: '/',
        httpOnly: true,
        sameSite: 'strict',
        secure: process.env.NODE_ENV === 'prod',
        maxAge: 60 * 60 * 24 * 30
    });
}
export const actions = {
    login: async ({cookies, request}: {cookies: Cookies, request: Request}) => {
        const data = await request.formData();

        const credentials: UserLoginPostRequest = {
            libreUser: {
                email: <string> data.get('email'),
                password: <string> data.get('password')
            }
        }

        const response = await api.userLoginPost(credentials);

        if (!response) {
            fail(401)
        }

        createSession(cookies, response);

        throw redirect(303, '/');
    }
}