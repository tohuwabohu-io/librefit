import { invoke } from '@tauri-apps/api/core';

/**
 * @param {function} fetchApi
 * @param {{path: String; method: String; contentType: String; guarded: boolean}} api
 * @param {any} [data]
 * @returns Response
 */
export const proxyFetch = async (fetchApi, api, data) => {
	return await invoke("greet", { name });
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
