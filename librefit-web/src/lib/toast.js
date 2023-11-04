/**
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} err
 */
export const handleApiError = (toastStore, err) => {
	console.error(err);

	toastStore.trigger({
		message: 'An error occured. Please try again later.',
		background: 'variant-filled-warning',
		autohide: false
	});
};

/**
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} toastMessage
 */
export const showToastSuccess = (toastStore, toastMessage) => {
	toastStore.trigger({
		message: toastMessage,
		background: 'variant-filled-primary',
		autohide: true
	});
};
