import { handleApiError, showToastSuccess } from '$lib/toast.js';

/**
 * @param e
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 * @param {number} id
 */
export const addEntry = (e, callback, toastStore, route, id) => {
	/** @type {CalorieTrackerEntry} */
	const newEntry = {
		id: id,
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
		id: e.detail.sequence,
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
			showToastSuccess(toastStore, 'Entry updated successfully!');
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

	if (params !== undefined) {
		query += `?sequence=${e.detail.sequence}&added=${e.detail.date}`;
	} else {
		query += `?${params}&sequence=${e.detail.sequence}&added=${e.detail.date}`;
	}

	fetch(query, {
		method: 'DELETE'
	})
		.then((response) => {
			if (response.status === 200) {
				callback(e.detail.date);
			} else {
				throw Error(response.status);
			}
		})
		.catch((e) => handleApiError(toastStore, e));
};
