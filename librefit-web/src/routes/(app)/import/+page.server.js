import { api } from '$lib/server/api/index.js';
import { proxyFetch } from '$lib/server/api/util.js';
import { fail } from '@sveltejs/kit';

/** @type {import('./$types').Actions} */
export const actions = {
	startImport: async (event) => {
		const importApi = api.postImportBulk;

		/** @type {FormData} */
		const formData = await event.request.formData();

		console.log(formData);

		const importerSelection = formData['importer'];

		/** @type File */
		const file = formData.get('file');

		console.log(file);

		if (file.type !== 'text/csv') {
			return fail(400, { errors: [{ field: 'file', message: 'Please choose a valid CSV file.' }] });
		}

		if (file.size > 32768) {
			return fail(400, {
				errors: [{ field: 'file', message: 'File size exceeds the limit of 32 KB.' }]
			});
		}

		/** @type ImportConfig */
		const config = {
			datePattern: formData['datePattern'],
			headerLength: formData['headerLength'],
			drop: formData['drop'],
			updateCalorieTracker: importerSelection !== 'W',
			updateWeightTracker: importerSelection !== 'C'
		};

		formData.append('config', JSON.stringify(config));

		formData.delete('datePattern');
		formData.delete('headerLength');
		formData.delete('drop');
		formData.delete('importer');

		const response = await proxyFetch(event.fetch, importApi, event.cookies.get('auth'), formData);

		let result;

		if (response.status === 200) {
			result = {
				success: true
			};
		} else {
			/** @type ErrorResponse */
			const errorResponse = await response.json();

			result = fail(response.status, errorResponse);
		}

		return result;
	}
};
