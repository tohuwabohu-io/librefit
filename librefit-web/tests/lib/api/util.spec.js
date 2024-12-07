import { expect, test } from 'vitest';
import { convertFormDataToJson, proxyFetch, replaceGetParamsJson } from '$lib/api/util.ts';
import { api } from '$lib/api/index.js';

test('proxyFetch should return valid response for API calls', async () => {
	let userApi = api.updateUserInfo;

	const data = {
		/* your test data based on the api */
	};

	// Mock the fetchApi function to simulate API call
	const fetchApi = async (_, options) => {
		expect(options.method).toBe(userApi.method);
		expect(options.credentials).toBe('include');
		expect(options.headers['content-type']).toBe(userApi.contentType);

		if (['POST', 'PUT'].includes(userApi.method.toUpperCase())) {
			expect(JSON.parse(options.body)).toEqual(data);
		}

		return {
			status: 200,
			statusText: 'OK'
		};
	};

	const response = await proxyFetch(fetchApi, userApi, data);

	expect(response.status).toBe(200);
});

test('replaceGetParamsJson should correctly interpolate path with JSON params', () => {
	const path = '/api/tracker/weight/list/{dateFrom}/{dateTo}';
	const params = { dateFrom: '2022-01-01', dateTo: '2022-01-10' };
	const expectedPath = '/api/tracker/weight/list/2022-01-01/2022-01-10';

	const result = replaceGetParamsJson(path, params);
	expect(result).toBe(expectedPath);
});

test('convertFormDataToJson should correctly convert FormData to JSON object', () => {
	const formData = new FormData();
	formData.append('username', 'testUser');
	formData.append('password', 'testPass');

	const expectedJson = { username: 'testUser', password: 'testPass' };

	const result = convertFormDataToJson(formData);
	expect(result).toEqual(expectedJson);
});
