import { vi } from 'vitest';
import { writable } from 'svelte/store';

/**
 * @typedef {Object} ModalStoreSettingsMock
 * @property {any} meta arbitrary (display) data passed to the modal
 * @property {Mock<any, any>} [callback] callback function (submit, close, ...)
 * @property {Mock<any, any>} [trigger] mock modalStore.trigger({})
 * @property {Mock<any, any>} [close] mock modalStore.close()
 * @property {Mock<any, any>} [clear] mock modalStore.clear()
 */

const defaultModalStoreMock = {
	...writable([
		{
			response: vi.fn(),
			meta: {}
		}
	]),
	trigger: vi.fn(),
	close: vi.fn(),
	clear: vi.fn()
};

let modalStoreMock = { ...defaultModalStoreMock };

/**
 * @param {ModalStoreSettingsMock} settings
 */
export const updateModalStoreMock = (settings) => {
	modalStoreMock = {
		...writable([
			{
				response: settings.callback ? settings.callback : vi.fn(),
				meta: settings.meta ? settings.meta : {}
			}
		]),
		trigger: settings.trigger ? settings.trigger : vi.fn(),
		close: settings.close ? settings.close : vi.fn(),
		clear: settings.clear ? settings.clear : vi.fn()
	};
};

export const extractModalStoreMockTriggerCallback = () => {
	// Get the 'response' function from the last call to the 'trigger' mock.
	const mockTrigger = modalStoreMock.trigger;
	const lastCallArgs = mockTrigger.mock.calls[mockTrigger.mock.calls.length - 1];

	return lastCallArgs[0].response;
};

vi.mock('@skeletonlabs/skeleton', async (importOriginal) => {
	const actualSkelton = await importOriginal();

	return {
		...actualSkelton,
		getModalStore: vi.fn(() => modalStoreMock),
		initializeModalStore: vi.fn(() => modalStoreMock)
	};
});
