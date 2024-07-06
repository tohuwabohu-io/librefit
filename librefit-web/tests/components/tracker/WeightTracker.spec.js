import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, getByText, render } from '@testing-library/svelte';
import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
import { convertDateStrToDisplayDateStr, getDateAsStr } from '$lib/date.js';
import { tick } from 'svelte';
import * as skeleton from '@skeletonlabs/skeleton';
import { extractModalStoreMockTriggerCallback } from '../../__mocks__/skeletonProxy.js';

const mockData = {
	weightList: [
		{ amount: 70, added: '2022-01-01', sequence: 1 },
		{ amount: 69, added: '2022-02-01', sequence: 2 }
	],
	currentGoal: { targetWeight: 60, endDate: '2023-01-01' }
};

/**
 * @vitest-environment jsdom
 */
describe('WeightTracker.svelte component', () => {
	afterEach(() => cleanup());

	it('renders correctly', async () => {
		const { getByText } = render(WeightTracker, mockData);

		expect(
			getByText(
				`Current weight: ${mockData.weightList[0].amount}kg (${convertDateStrToDisplayDateStr(mockData.weightList[0].added)})`
			)
		).toBeDefined();

		expect(
			getByText(
				`Goal: ${mockData.currentGoal.targetWeight}kg @ (${convertDateStrToDisplayDateStr(mockData.currentGoal.endDate)})`
			)
		).toBeDefined();
	});

	it('renders an empty component correctly', () => {
		const { getByText } = render(WeightTracker);

		expect(getByText('Nothing tracked for today. Now would be a good moment!')).toBeDefined();
		expect(getByText(`No goal set up.`)).toBeDefined();
	});

	it('should trigger the quick add button and dispatch addWeight', async () => {
		const { component, getByText, getByRole } = render(WeightTracker);

		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});
		component.$on('addWeight', dispatchMock);

		const amountInput = getByRole('spinbutton', { name: 'amount' });
		await fireEvent.input(amountInput, { target: { value: 72 } });

		const quickAddButton = getByRole('button', { name: 'add' });
		await fireEvent.click(quickAddButton);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(1);
		expect(dispatchEvent).toEqual({
			callback: expect.any(Function),
			value: 72,
			dateStr: getDateAsStr(new Date()),
			target: undefined
		});
	});

	it('should trigger the edit button and dispatch updateWeight', async () => {
		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});

		const { component, getByText } = render(WeightTracker, mockData);

		component.$on('updateWeight', dispatchMock);

		const updateWeightButton = getByText('Edit');
		expect(updateWeightButton).toBeTruthy();
		await fireEvent.click(updateWeightButton);
		await tick();

		expect(skeleton.getModalStore().trigger).toHaveBeenCalledWith({
			type: 'component',
			component: 'weightModal',
			response: expect.any(Function),
			meta: {
				weightList: mockData.weightList
			}
		});

		const callback = extractModalStoreMockTriggerCallback();

		await callback(undefined);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		const callbackDetails = {
			dateStr: getDateAsStr(new Date()),
			value: 71,
			sequence: 2
		};

		const callbackParams = {
			detail: {
				type: 'update',
				detail: callbackDetails
			}
		};

		await callback(callbackParams);

		expect(dispatchMock).toHaveBeenCalledTimes(1);
		expect(dispatchEvent).toEqual(callbackDetails);
	});

	it('should trigger the edit button and dispatch deleteWeight', async () => {
		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});

		const { component, getByText } = render(WeightTracker, mockData);

		component.$on('deleteWeight', dispatchMock);

		const updateWeightButton = getByText('Edit');
		expect(updateWeightButton).toBeTruthy();
		await fireEvent.click(updateWeightButton);
		await tick();

		const callback = extractModalStoreMockTriggerCallback();
		await callback(undefined);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		const callbackDetails = {
			dateStr: getDateAsStr(new Date()),
			sequence: 2
		};

		const callbackParams = {
			detail: {
				type: 'remove',
				detail: callbackDetails
			}
		};

		await callback(callbackParams);

		expect(dispatchMock).toHaveBeenCalledTimes(1);
		expect(dispatchEvent).toEqual(callbackDetails);
	});
});
