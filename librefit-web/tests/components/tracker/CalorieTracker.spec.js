import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, getByRole, getByText, render, screen } from '@testing-library/svelte';
import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
import * as skeleton from '@skeletonlabs/skeleton';
import { tick } from 'svelte';
import { extractModalStoreMockTriggerCallback } from '../../__mocks__/skeletonProxy.js';
import { getDateAsStr, getDaytimeFoodCategory } from '$lib/date.js';

const mockCategories = [
	{ shortvalue: 'b', longvalue: 'Breakfast', visible: true },
	{ shortvalue: 'l', longvalue: 'Lunch', visible: true },
	{ shortvalue: 'd', longvalue: 'Dinner', visible: true },
	{ shortvalue: 't', longvalue: 'Treat', visible: true },
	{ shortvalue: 's', longvalue: 'Snack', visible: true }
];

const mockEntries = [
	{ added: '2023-11-10', sequence: 1, amount: 500, category: 'b' },
	{ added: '2023-11-10', sequence: 2, amount: 300, category: 'l' },
	{ added: '2023-11-10', sequence: 3, amount: 600, category: 'd' },
	{ added: '2023-11-10', sequence: 4, amount: 200, category: 't' },
	{ added: '2023-11-10', sequence: 5, amount: 150, category: 's' }
];

/** @type CalorieTarget */
const mockCalorieTarget = {
	added: '2023-01-01',
	sequence: 1,
	startDate: '2023-01-01',
	endDate: '2023-12-31',
	targetCalories: 2000,
	maximumCalories: 2400
};

/**
 * @vitest-environment jsdom
 */
describe('CalorieTracker.svelte component', () => {
	afterEach(() => cleanup());

	// Test that the CalorieTracker component renders correctly
	it('renders correctly', () => {
		const { getByText, getByRole, getByTestId } = render(CalorieTracker, {
			categories: mockCategories,
			calorieTrackerEntries: mockEntries,
			calorieTarget: mockCalorieTarget
		});

		// heading
		expect(getByText('Calorie Tracker')).toBeTruthy();

		// radial
		expect(getByTestId('progress-radial')).toBeDefined();

		// deficit/surplus message
		expect(getByText(`You still have 250kcal left for the day. Good job!`));

		const amountInput = getByRole('spinbutton', { name: 'amount' });

		// tracker
		expect(getByText('kcal')).toBeDefined();
		expect(amountInput.placeholder).toEqual('Amount...');
		expect(amountInput.value).toBeFalsy();

		// quickadd
		expect(getByRole('button', { name: 'add calories' })).toBeTruthy();

		// button group
		expect(getByText('Add')).toBeTruthy();
		expect(getByText('Edit')).toBeTruthy();
	});

	it('should trigger the quick add button', async () => {
		const { component, getByText, getByRole } = render(CalorieTracker);

		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});
		component.$on('addCalories', dispatchMock);

		const amountInput = getByRole('spinbutton', { name: 'amount' });
		await fireEvent.input(amountInput, { target: { value: 100 } });

		const quickAddButton = getByRole('button', { name: 'add' });
		await fireEvent.click(quickAddButton);
		await tick();

		expect(dispatchMock).toHaveBeenCalledTimes(1);
		expect(dispatchEvent).toEqual({
			callback: expect.any(Function),
			value: 100,
			category: getDaytimeFoodCategory(new Date()),
			dateStr: getDateAsStr(new Date()),
			target: undefined
		});
	});

	it('should trigger the add button and dispatch addCalories', async () => {
		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});

		const { component, getByText } = render(CalorieTracker);
		component.$on('addCalories', dispatchMock);

		// Expect that the add button is rendered
		const addButton = getByText('Add');
		await fireEvent.click(addButton);
		await tick();

		expect(skeleton.getModalStore().trigger).toHaveBeenCalledWith({
			type: 'component',
			component: 'trackerModal',
			response: expect.any(Function),
			meta: {
				categories: undefined
			}
		});

		const callback = extractModalStoreMockTriggerCallback();
		await callback({
			detail: {
				close: true
			}
		});

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		const callbackDetails = {
			dateStr: getDateAsStr(new Date()),
			value: 100,
			category: getDaytimeFoodCategory(new Date())
		};

		const callbackParams = {
			detail: {
				type: 'add',
				detail: callbackDetails
			}
		};

		await callback(callbackParams);

		expect(dispatchMock).toHaveBeenCalledTimes(1);
		expect(dispatchEvent).toEqual(callbackDetails);
	});

	it('should trigger the edit button and dispatch updateCalories', async () => {
		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});

		const { component, getByText } = render(CalorieTracker, {
			categories: mockCategories,
			calorieTrackerEntries: mockEntries,
			calorieTarget: mockCalorieTarget
		});
		component.$on('updateCalories', dispatchMock);

		const editButton = getByText('Edit');
		await fireEvent.click(editButton);
		await tick();

		expect(skeleton.getModalStore().trigger).toHaveBeenCalledWith({
			type: 'component',
			component: 'trackerModal',
			response: expect.any(Function),
			meta: {
				categories: mockCategories,
				entries: mockEntries
			}
		});

		const callback = extractModalStoreMockTriggerCallback();
		await callback({
			detail: {
				close: true
			}
		});

		expect(dispatchMock).toHaveBeenCalledTimes(0);

		const callbackDetails = {
			dateStr: getDateAsStr(new Date()),
			value: 100,
			sequence: 2,
			category: getDaytimeFoodCategory(new Date())
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

	it('should trigger the edit button and dispatch deleteCalories', async () => {
		let dispatchEvent;
		const dispatchMock = vi.fn((e) => {
			dispatchEvent = e.detail;
		});

		const { component, getByText } = render(CalorieTracker, {
			categories: mockCategories,
			calorieTrackerEntries: mockEntries,
			calorieTarget: mockCalorieTarget
		});

		component.$on('deleteCalories', dispatchMock);

		const editButton = getByText('Edit');
		await fireEvent.click(editButton);
		await tick();

		const callback = extractModalStoreMockTriggerCallback();

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
