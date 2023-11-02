import { redirect } from '@sveltejs/kit';

/** @type {import('./$types').PageServerLoad} */
export const load = async ({ fetch, cookies }) => {
	console.log('logout');

	cookies.delete('auth');

	throw redirect(303, '/login');
};
