import { Configuration, TdeeResourceApi } from 'librefit-api/rest';
import { PUBLIC_API_BASE_PATH } from '$env/static/public';

export const actions = {
	default: async ({ cookies, request }) => {
		const tdeeApi = new TdeeResourceApi(
			new Configuration({
				basePath: PUBLIC_API_BASE_PATH
			})
		);

		const data = await request.formData();

		const activityLevel = data.get('activityLevel');
		const age = data.get('age');
		const height = data.get('height');
		const sex = data.get('sex');
		const weight = data.get('weight');
		const diff = data.get('diff');
		const gain = data.get('gain');

		return await tdeeApi
			.tdeeCalculateAgeSexWeightHeightActivityLevelDiffGainGet({
				age: age,
				activityLevel: activityLevel,
				height: height,
				sex: sex,
				weight: weight,
				diff: diff,
				gain: gain
			})
			.catch((error) => console.error(error));
	}
};
