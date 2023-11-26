import { redirect } from '@sveltejs/kit';
import { login } from '$lib/server/api/login.js';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	/** @type String | undefined */
	const jwt = cookies.get('auth');

	if (jwt) {
		throw redirect(303, '/dashboard');
	}
};

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		return login(event);
	}
};
