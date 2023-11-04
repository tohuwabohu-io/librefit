import * as ct_crud from '$lib/server/api/calories-rest.js';
import * as weight_crud from '$lib/server/api/weight-rest.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const GET = async ({ fetch, url, cookies }) => {
	/** @type {Response} */
	let response;

	const type = url.searchParams.get('type');

	if (type === 'ct') {
		response = ct_crud.GET({ fetch, url, cookies });
	} else if (type === 'goal' || type === 'weight') {
		response = weight_crud.GET({ fetch, url, cookies });
	} else {
		console.error(`dashboard api can't be called with type ${type}`);

		response = new Response(null, {
			status: 422,
			statusText: 'Unprocessable Content'
		});
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const POST = async ({ request, fetch, cookies }) => {
	const peek = request.clone();
	const payload = await peek.json();

	/** @type {Response} */
	let response;

	if (payload['goal'] || payload['weight']) {
		response = weight_crud.POST({ request, fetch, cookies });
	} else {
		response = ct_crud.POST({ request, fetch, cookies });
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const PUT = async ({ request, fetch, cookies }) => {
	const peek = request.clone();
	const payload = await peek.json();

	/** @type {Response} */
	let response;

	if (payload['goal'] || payload['weight']) {
		response = weight_crud.PUT({ request, fetch, cookies });
	} else {
		response = ct_crud.PUT({ request, fetch, cookies });
	}

	return response;
};

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const DELETE = async ({ fetch, url, cookies }) => {
	/** @type {Response} */
	let response;

	const type = url.searchParams.get('type');

	if (type === 'ct') {
		response = ct_crud.DELETE({ fetch, url, cookies });
	} else if (type === 'goal' || type === 'weight') {
		response = weight_crud.DELETE({ fetch, url, cookies });
	} else {
		console.error(`dashboard api can't be called with type ${type}`);

		response = new Response(null, {
			status: 422,
			statusText: 'Unprocessable Content'
		});
	}

	return response;
};
