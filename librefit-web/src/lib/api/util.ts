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

export const replaceGetParamsJson = (path: string, json: any) => {
	if (json) {
		Object.entries(json).forEach(([key, value]) => {
			path = path.replace(`{${key}}`, String(value));
		});
	}

	return path;
};

export const convertFormDataToJson = (formData: FormData): any => {
	const json: any = {};
	formData.forEach((value, key) => (json[key] = value));

	return json;
};
