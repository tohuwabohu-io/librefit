import { env } from '$env/dynamic/public';
import { error } from '@sveltejs/kit';

/**
 * @param {function} fetchApi
 * @param {{path: String; method: String; contentType: String; guarded: boolean}} api
 * @param {any} [data]
 * @returns Response
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
		if (import.meta.env.MODE === 'development') {
			console.log(`method ${api.method} not implemented`);
		}

		throw error(405);
	}

	try {
		if (import.meta.env.MODE === 'development') {
			console.log(`${method} ${env.PUBLIC_API_BASE_PATH + path}`);
		}

		response = await call;

		if (import.meta.env.MODE === 'development') {
			console.log(
				`${method} ${path} statusCode=${response.status} statusText=${response.statusText}`
			);
		}
	} catch (e) {
		if (import.meta.env.MODE === 'development') {
			console.error(
				`${method} ${path} statusCode=${response.status} statusText=${response.statusText}`
			);
		}

		console.error(e);

		response = new Response();
	}

	if (response.status === 401) window.location.replace('/?session_expired=true');

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
