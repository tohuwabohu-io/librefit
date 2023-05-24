import {TdeeResourceApi} from 'librefit-api/rest';
import {DEFAULT_CONFIG} from '$lib/api/Config';

export const actions = {
	default: async ({ cookies, request }) => {
		const tdeeApi = new TdeeResourceApi(DEFAULT_CONFIG);

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
