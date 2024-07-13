import { cleanup, fireEvent, render } from '@testing-library/svelte';
import { afterEach, describe, expect, it, vi } from 'vitest';
import CalorieTargetModal from '$lib/components/modal/CalorieTargetModal.svelte';
import { tick } from 'svelte';
import { addYears } from 'date-fns';

import { updateModalStoreMock } from '../../__mocks__/skeletonProxy.js';
import { getDateAsStr } from '$lib/date.js';

/**
 * @vitest-environment jsdom
 */
describe('CalorieTargetModal.svelte component', () => {
	afterEach(() => cleanup());

	/**
	 * Given a component with empty fields
	 * - fill in the form and click 'submit'
	 * - response type for event detail containing filled-in calorieTarget must have been triggered
	 */
	it('should fill in form and submit', async () => {
		const mockData = {};

		const responseCallback = vi.fn();
		updateModalStoreMock({ meta: mockData, callback: responseCallback });

		const { getByRole } = render(CalorieTargetModal);
		await fireEvent.input(getByRole('spinbutton', { name: 'Target intake kcal' }), {
			target: { value: 1800 }
		});
		await fireEvent.input(getByRole('spinbutton', { name: 'Maximum intake kcal' }), {
			target: { value: 2000 }
		});
		await fireEvent.click(getByRole('button', { name: 'Submit' }));
		await tick();

		expect(responseCallback).toHaveBeenCalledTimes(1);

		const expectedDetail = {
			calorieTarget: {
				added: getDateAsStr(new Date()),
				startDate: getDateAsStr(new Date()),
				targetCalories: '1800',
				endDate: getDateAsStr(addYears(new Date(), 1)),
				maximumCalories: '2000'
			}
		};

		expect(responseCallback).toHaveBeenCalledWith(expectedDetail);
	});

	/**
	 * Given a component receives the 'currentTarget' from meta of $modalStore[0]
	 * - Render modal
	 * - assert prefilled values
	 */
	it('should accept currentTarget from props', async () => {
		const mockData = {
			currentTarget: {
				added: '2024-05-01',
				startDate: '2024-05-01',
				endDate: '2025-05-01',
				targetCalories: '1800',
				maximumCalories: '2000'
			}
		};

		updateModalStoreMock({ meta: mockData });

		const { getByRole } = render(CalorieTargetModal);

		expect(getByRole('spinbutton', { name: 'Target intake kcal' }).value).toBe(
			mockData.currentTarget.targetCalories
		);
		expect(getByRole('spinbutton', { name: 'Maximum intake kcal' }).value).toBe(
			mockData.currentTarget.maximumCalories
		);
	});

	/**
	 * Given a component with empty fields
	 * - Click 'cancel'
	 * - response type for event detail containing cancelled attribute set to true must have been triggered
	 */
	it('should cancel the action', async () => {
		const mockData = {};

		const responseCallback = vi.fn();
		updateModalStoreMock({ meta: mockData, callback: responseCallback });

		const { getByRole } = render(CalorieTargetModal);

		await fireEvent.click(getByRole('button', { name: 'Cancel' }));
		await tick();

		expect(responseCallback).toHaveBeenCalledTimes(1);

		const expectedDetail = { cancelled: true };

		expect(responseCallback).toHaveBeenCalledWith(expectedDetail);
	});
});
