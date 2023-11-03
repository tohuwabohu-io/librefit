import * as ct_crud from '$lib/server/calories-rest.js';

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const GET = ct_crud.GET;

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const POST = ct_crud.POST;

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const PUT = ct_crud.PUT;

/**
 * @type {import('@sveltejs/kit').RequestHandler}
 */
export const DELETE = ct_crud.DELETE;
