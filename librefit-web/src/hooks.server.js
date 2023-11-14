import { redirect } from '@sveltejs/kit';

const unguardedRoutes = ['/', '/login', '/register'];

/** @type {import('@sveltejs/kit').Handle} */
export async function handle({ event, resolve }) {
	if (!unguardedRoutes.includes(event.url.pathname)) {
		if (!event.cookies.get('auth') || event.cookies.get('auth') === 'undefined') {
			throw redirect(303, '/');
		}
	}

	return await resolve(event);
}
