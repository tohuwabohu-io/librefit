/**
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} err
 */
export const showToastError = (toastStore, err) => {
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

/**
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} toastMessage
 */
export const showToastInfo = (toastStore, toastMessage) => {
	toastStore.trigger({
		message: toastMessage,
		background: 'variant-filled-tertiary',
		autohide: true
	});
};

/**
 * @param {import('@skeletonlabs/skeleton').ToastStore} toastStore
 * @param {String} warning
 */
export const showToastWarning = (toastStore, warning) => {
	toastStore.trigger({
		message: warning,
		background: 'variant-filled-warning',
		autohide: true
	});
};
