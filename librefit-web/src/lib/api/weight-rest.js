import { handleApiError, showToastSuccess } from '$lib/toast.js';

/**
 * @param e
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 */
export const add = (e, callback, toastStore, route) => {
	fetch(route, {
		method: 'POST',
		body: JSON.stringify({
			weight: {
				id: e.detail.sequence,
				added: e.detail.todayDateStr,
				amount: e.detail.value
			}
		}),
		headers: {
			'Content-Type': 'application/json'
		}
	})
		.then((response) => {
			callback(response);

			showToastSuccess(toastStore, 'Successfully added weight.');
		})
		.catch((e) => handleApiError(toastStore, e));
};

/**
 * @param e
 * @param {Function} callback
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} route
 */
export const update = (e, callback, toastStore, route) => {
	fetch(route, {
		method: 'PUT',
		body: JSON.stringify({
			weight: {
				id: e.detail.sequence,
				date: e.detail.date,
				amount: e.detail.value
			}
		})
	})
		.then((response) => {
			callback(response);

			showToastSuccess(toastStore, 'Successfully updated weight.');
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
export const remove = (e, callback, toastStore, route, params) => {
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
			callback(response);

			showToastSuccess(toastStore, 'Successfully removed weight.');
		})
		.catch((e) => handleApiError(toastStore, e));
};
