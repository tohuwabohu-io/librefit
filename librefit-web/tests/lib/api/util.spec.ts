import { expect, test } from 'vitest';
import { convertFormDataToJson, replaceGetParamsJson } from '../../../src/lib/api/util';

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
