import { registerUser } from '$lib/server/register.js';

/** @type {import('./$types').Actions} */
export const actions = {
	register: async (event) => {
		return registerUser(event);
	}
};
