import { showToastError, showToastSuccess, showToastInfo, showToastWarning } from './toast';
import { expect, test, vi } from 'vitest';

test('showToastError should trigger toastStore with correct params', () => {
	const mockToastStore = { trigger: vi.fn() };
	const errorMessage = 'Some error'; // this will show up in the console

	showToastError(mockToastStore, errorMessage);
	expect(mockToastStore.trigger).toHaveBeenCalledWith({
		message: 'An error occurred. Please try again later.',
		background: 'variant-filled-warning',
		autohide: false
	});
});

test('showToastSuccess should trigger toastStore with correct params', () => {
	const mockToastStore = { trigger: vi.fn() };
	const successMessage = 'Success!';

	showToastSuccess(mockToastStore, successMessage);
	expect(mockToastStore.trigger).toHaveBeenCalledWith({
		message: successMessage,
		background: 'variant-filled-primary',
		autohide: true
	});
});

test('showToastInfo should trigger toastStore with correct params', () => {
	const mockToastStore = { trigger: vi.fn() };
	const infoMessage = 'Info message';

	showToastInfo(mockToastStore, infoMessage);
	expect(mockToastStore.trigger).toHaveBeenCalledWith({
		message: infoMessage,
		background: 'variant-filled-tertiary',
		autohide: true
	});
});

test('showToastWarning should trigger toastStore with correct params', () => {
	const mockToastStore = { trigger: vi.fn() };
	const warningMessage = 'Warning!';

	showToastWarning(mockToastStore, warningMessage);
	expect(mockToastStore.trigger).toHaveBeenCalledWith({
		message: warningMessage,
		background: 'variant-filled-warning',
		autohide: true
	});
});
