import * as weight_crud from '$lib/server/weight-rest.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const GET = weight_crud.GET;

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const POST = weight_crud.POST;

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const PUT = weight_crud.PUT;

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const DELETE = weight_crud.DELETE;
