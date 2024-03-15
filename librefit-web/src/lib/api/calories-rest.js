import { showToastError, showToastSuccess } from '$lib/toast.js';
import { Category } from '$lib/api/model.js';
import { categoriesAsKeyValue, getCategoryValueAsKey } from '$lib/util.js';

/**
 * @param event
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 */
export const addEntry = (event, callback, toastStore, route) => {
	/** @type {CalorieTrackerEntry} */
	const newEntry = {
		added: event.detail.date,
		amount: event.detail.value,
		category: event.detail.category
	};

	fetch(route, {
		method: 'POST',
		body: JSON.stringify(newEntry)
	})
		.then(async (_) => {
			await callback(newEntry.added, event.detail.callback);

			showToastSuccess(
				toastStore,
				`Successfully added ${
					newEntry.category !== Category.Unset
						? getCategoryValueAsKey(newEntry.category)
						: 'calories'
				}.`
			);
		})
		.catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback();
		});
};

/**
 * @param event
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 */
export const updateEntry = (event, callback, toastStore, route) => {
	/** @type {CalorieTrackerEntry} */
	const entry = {
		sequence: event.detail.sequence,
		added: event.detail.date,
		amount: event.detail.value,
		category: event.detail.category
	};

	fetch(route, {
		method: 'PUT',
		body: JSON.stringify(entry)
	})
		.then(async (_) => {
			await callback(entry.added, event.detail.callback);

			showToastSuccess(
				toastStore,
				`Successfully updated ${
					entry.category !== Category.Unset ? getCategoryValueAsKey(entry.category) : 'calories'
				}`
			);
		})
		.catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback();
		});
};

/**
 * @param event
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 * @param {String} [params]
 */
export const deleteEntry = (event, callback, toastStore, route, params) => {
	let query = route;

	if (params === undefined) {
		query += `?sequence=${event.detail.sequence}&added=${event.detail.date}`;
	} else {
		query += `?${params}&sequence=${event.detail.sequence}&added=${event.detail.date}`;
	}

	fetch(query, {
		method: 'DELETE'
	})
		.then(async (response) => {
			if (response.status === 200) {
				await callback(event.detail.date, event.detail.callback);

				showToastSuccess(toastStore, 'Deletion successful.');
			} else {
				throw Error(await response.json());
			}
		})
		.catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback();
		});
};
