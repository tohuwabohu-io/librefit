import { PUBLIC_API_BASE_PATH } from '$env/static/public';
import { error } from '@sveltejs/kit';

/**
 * This is to be used in +server.js files.
 */

/**
 * @param {function} fetchApi
 * @param {{path: String; method: String; contentType: String;}} api
 * @param {any} data
 * @param {String | null} jwt
 */
export const proxyFetch = async (fetchApi, api, data, jwt) => {
	let response;
	let call;

	/** @type {any} */
	const headers = {
		'content-type': api.contentType
	};

	if (jwt) {
		headers['Authorization'] = `Bearer ${jwt}`;
	}

	if (api.method.toUpperCase() === 'POST') {
		call = fetchApi(PUBLIC_API_BASE_PATH + api.path, {
			method: api.method,
			headers,
			body: JSON.stringify(data)
		});
	} else if (api.method.toUpperCase() === 'GET') {
		call = fetchApi(PUBLIC_API_BASE_PATH + replaceGetParamsJson(api.path, data), {
			method: api.method,
			headers
		});
	} else {
		console.log(`method ${api.method} not implemented`);
		throw error(405);
	}

	try {
		response = await call;
	} catch (e) {
		console.log(e);
		response = new Response();
	}

	return response;
};

/**
 * @param {String} path
 * @param {any} json
 */
export const replaceGetParamsJson = (path, json) => {
	Object.entries(json).forEach(([key, value]) => {
		path = path.replace(`{${key}}`, String(value));
	});

	return path;
};

/**
 * @param {FormData} formData
 */
export const convertFormDataToJson = (formData) => {};
