import { env } from '$env/dynamic/public';
import { error, redirect } from '@sveltejs/kit';
import { goto } from '$app/navigation';

/**
 * @param {function} fetchApi
 * @param {{path: String; method: String; contentType: String; guarded: boolean}} api
 * @param {any} [data]
 */
export const proxyFetch = async (fetchApi, api, data) => {
	let response;
	let call;

	const method = api.method.toUpperCase();
	let path = api.path;

	/** @type {any} */
	let headers = {
		'content-type': api.contentType
	};

	if (api.contentType === 'multipart/form-data') {
		headers = {};

		if (method === 'POST') {
			call = fetchApi(env.PUBLIC_API_BASE_PATH + path, {
				method: api.method,
				credentials: 'include',
				headers,
				body: data
			});
		}
	} else {
		if (method === 'POST' || method === 'PUT') {
			call = fetchApi(env.PUBLIC_API_BASE_PATH + path, {
				method: api.method,
				credentials: 'include',
				headers,
				body: JSON.stringify(data)
			});
		} else if (method === 'GET' || method === 'DELETE') {
			path = replaceGetParamsJson(path, data);

			call = fetchApi(env.PUBLIC_API_BASE_PATH + path, {
				method: api.method,
				credentials: 'include',
				headers
			});
		}
	}

	if (!call) {
		console.log(`method ${api.method} not implemented`);
		throw error(405);
	}

	try {
		console.log(`${method} ${env.PUBLIC_API_BASE_PATH + path}`);

		response = await call;

		console.log(
			`${method} ${path} statusCode=${response.status} statusText=${response.statusText}`
		);
	} catch (e) {
		console.error(
			`${method} ${path} statusCode=${response.status} statusText=${response.statusText}`
		);

		console.log(e);

		response = new Response();
	}

	if (response.status === 401) throw redirect(303, '/?expired');

	return response;
};

/**
 * @param {String} path
 * @param {any} json
 */
export const replaceGetParamsJson = (path, json) => {
	if (json) {
		Object.entries(json).forEach(([key, value]) => {
			path = path.replace(`{${key}}`, String(value));
		});
	}

	return path;
};

/**
 * @param {FormData} formData
 */
export const convertFormDataToJson = (formData) => {
	/** @type {any} */
	const json = {};
	formData.forEach((value, key) => (json[key] = value));

	return json;
};
