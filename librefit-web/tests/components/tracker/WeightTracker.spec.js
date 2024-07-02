import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, getByText, render } from '@testing-library/svelte';
import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
import { convertDateStrToDisplayDateStr } from '$lib/date.js';
import { tick } from 'svelte';
import * as skeleton from '@skeletonlabs/skeleton';
import { extractModalStoreMockTriggerCallback } from '../../__mocks__/skeletonProxy.js';

const mockData = {
	lastEntry: { amount: 70, added: '2022-01-01' },
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
				`Current weight: ${mockData.lastEntry.amount}kg (${convertDateStrToDisplayDateStr(mockData.lastEntry.added)})`
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

		expect(getByText('Nothing tracked yet. Today is a good day to start!')).toBeDefined();
		expect(getByText(`No goal set up.`)).toBeDefined();
	});

	it('should trigger the update weight button and dispatch addWeight', async () => {
		const { component, getByText } = render(WeightTracker, mockData);

		const dispatchMock = vi.fn();
		component.$on('addWeight', dispatchMock);

		const updateWeightButton = getByText('Set weight');
		expect(updateWeightButton).toBeTruthy();
		await fireEvent.click(updateWeightButton);
		await tick();

		expect(skeleton.getModalStore().trigger).toHaveBeenCalledWith({
			type: 'component',
			component: 'weightModal',
			response: expect.any(Function)
		});

		const callback = extractModalStoreMockTriggerCallback();

		await callback(undefined);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		await callback({
			dateStr: mockData.lastEntry.added,
			detail: {
				value: mockData.lastEntry.amount
			}
		});
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(1);
	});

	it('should trigger the update target button and dispatch updateGoal', async () => {
		const dispatchMock = vi.fn();

		const { component, getByText } = render(WeightTracker, mockData);
		component.$on('updateGoal', dispatchMock);

		const updateWeightButton = getByText('Set target');
		expect(updateWeightButton).toBeTruthy();
		await fireEvent.click(updateWeightButton);
		await tick();

		expect(skeleton.getModalStore().trigger).toHaveBeenCalledWith({
			type: 'component',
			component: 'goalModal',
			meta: { goal: mockData.currentGoal },
			response: expect.any(Function)
		});

		const callback = extractModalStoreMockTriggerCallback();

		await callback({
			cancelled: true
		});
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		await callback(mockData.currentGoal);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(1);
	});
});
