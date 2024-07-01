import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, getByText, render } from '@testing-library/svelte';
import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
import { convertDateStrToDisplayDateStr } from '$lib/date.js';
import { tick } from 'svelte';
import * as skeleton from '@skeletonlabs/skeleton';

vi.mock('@skeletonlabs/skeleton', () => ({
	...vi.importActual('@skeletonlabs/skeleton'),
	getModalStore: vi.fn().mockReturnValue({
		trigger: vi.fn(),
		close: vi.fn()
	})
}));

const mockData = {
	lastEntry: { amount: 70, added: '2022-01-01' },
	currentGoal: { targetWeight: 60, endDate: '2023-01-01' }
};

const extractResponseFn = () => {
	// Get the 'response' function from the last call to the 'trigger' mock.
	const mockTrigger = skeleton.getModalStore().trigger;
	const lastCallArgs = mockTrigger.mock.calls[mockTrigger.mock.calls.length - 1];

	return lastCallArgs[0].response;
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

		const responseFn = extractResponseFn();

		await responseFn(undefined);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		await responseFn({
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

		const responseFn = extractResponseFn();

		await responseFn({
			cancelled: true
		});
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		await responseFn(mockData.currentGoal);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(1);
	});
});
