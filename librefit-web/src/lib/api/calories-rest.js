import { handleApiError, showToastSuccess } from '$lib/toast.js';
import { Category } from '$lib/api/model.js';
import { categoriesAsKeyValue, getCategoryValueAsKey } from '$lib/util.js';

/**
 * @param e
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 */
export const addEntry = (e, callback, toastStore, route) => {
	/** @type {CalorieTrackerEntry} */
	const newEntry = {
		added: e.detail.date,
		amount: e.detail.value,
		category: e.detail.category
	};

	fetch(route, {
		method: 'POST',
		body: JSON.stringify(newEntry)
	})
		.then((_) => {
			callback(newEntry.added);
			showToastSuccess(
				toastStore,
				`Successfully added ${
					newEntry.category !== Category.Unset
						? getCategoryValueAsKey(newEntry.category)
						: 'calories'
				}.`
			);
		})
		.catch((e) => handleApiError(toastStore, e));
};

/**
 * @param e
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 */
export const updateEntry = (e, callback, toastStore, route) => {
	/** @type {CalorieTrackerEntry} */
	const entry = {
		sequence: e.detail.sequence,
		added: e.detail.date,
		amount: e.detail.value,
		category: e.detail.category
	};

	fetch(route, {
		method: 'PUT',
		body: JSON.stringify(entry)
	})
		.then((_) => {
			callback(entry.added);
			showToastSuccess(
				toastStore,
				`Successfully updated ${
					entry.category !== Category.Unset ? getCategoryValueAsKey(entry.category) : 'calories'
				}`
			);
		})
		.catch((e) => handleApiError(toastStore, e));
};

/**
 * @param e
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 * @param {String} [params]
 */
export const deleteEntry = (e, callback, toastStore, route, params) => {
	let query = route;

	if (params === undefined) {
		query += `?sequence=${e.detail.sequence}&added=${e.detail.date}`;
	} else {
		query += `?${params}&sequence=${e.detail.sequence}&added=${e.detail.date}`;
	}

	fetch(query, {
		method: 'DELETE'
	})
		.then((response) => {
			if (response.status === 200) {
				showToastSuccess(toastStore, 'Deletion successful.');
				callback(e.detail.date);
			} else {
				throw Error(response.status);
			}
		})
		.catch((e) => handleApiError(toastStore, e));
};
