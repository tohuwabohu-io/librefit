import { TdeeResourceApi } from 'librefit-api/rest/api';

export const actions = {
	default: async ({ cookies, request }) => {
		let result = {
			status: 404,
			tdee: null
		};

		const tdeeApi = new TdeeResourceApi(undefined, 'http://127.0.0.1:8080');

		const data = await request.formData();

		const activityLevel = data.get('activityLevel');
		const age = data.get('age');
		const height = data.get('height');
		const sex = data.get('sex');
		const weight = data.get('weight');
		const diff = data.get('diff') / 10;
		const gain = data.get('gain');

		try {
			const response = await tdeeApi.tdeeCalculateAgeSexWeightHeightActivityLevelDiffGainGet(
				activityLevel,
				age,
				diff,
				gain,
				height,
				sex,
				weight,
			);

			result = {
				status: response.status,
				tdee: response.data
			};

			console.log(response.status);
			console.log(response.data);
		} catch (e) {
			console.log(e.statusCode);
			console.log(e.message);

			result.status = e.statusCode;
		}

		return result;
	}
};
