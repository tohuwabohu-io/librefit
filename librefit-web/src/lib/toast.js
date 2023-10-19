import { toastStore } from '@skeletonlabs/skeleton';

export const handleApiError = (err) => {
	console.error(err);

	toastStore.trigger({
		message: 'An error occured. Please try again later.',
		background: 'variant-filled-warning',
		autohide: false
	});
};

export const showToastSuccess = (toastMessage) => {
	toastStore.trigger({
		message: toastMessage,
		background: 'variant-filled-primary',
		autohide: true
	});
};
