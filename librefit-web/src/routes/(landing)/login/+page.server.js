import { login } from '$lib/server/api/login.js';

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		return login(event);
	}
};
