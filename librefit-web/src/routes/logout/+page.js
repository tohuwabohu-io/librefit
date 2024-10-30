import { redirect } from '@sveltejs/kit';
import { logout } from '$lib/api/user.js';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch }) => {
	await logout();

	throw redirect(303, '/');
};
