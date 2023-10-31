/** @type {import('./$types').LayoutServerLoad} */
export async function load({ cookies }) {
	return {
		authenticated: cookies.get('auth') !== undefined
	};
}
