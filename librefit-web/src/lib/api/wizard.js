import { proxyFetch } from '$lib/api/util.js';
import { api } from '$lib/api/index.js';

/**
 * @param wizard {Wizard}
 * @returns {Promise<void>}
 */
export const postWizardResult = async (wizard) => {
	return proxyFetch(fetch, api.postWizardResult, wizard).then(async (response) => {
		if (response.ok) {
			return Promise.resolve();
		} else throw response;
	});
};
