import { registerUser } from '$lib/server/register.js';

/** @type {import('./$types').Actions} */
export const actions = {
	default: async (event) => {
		return registerUser(event);
	}
};
