import { cleanup, fireEvent, render } from '@testing-library/svelte';
import { afterEach, describe, expect, it, vi } from 'vitest';
import WeightTargetModal from '$lib/components/modal/WeightTargetModal.svelte';
import { tick } from 'svelte';
import { addYears } from 'date-fns';
import { updateModalStoreMock } from '../../__mocks__/skeletonProxy.js';
import { getDateAsStr } from '$lib/date.js';
/**
 * @vitest-environment jsdom
 */
describe('WeightTargetModal.svelte component', () => {
	afterEach(() => cleanup());

	/**
	 * Given a component with empty fields
	 * - fill in the form and click 'submit'
	 * - response type for event detail containing filled-in weightTarget must have been triggered
	 */
	it('should fill in form and submit', async () => {
		const mockData = {};

		const responseCallback = vi.fn();
		updateModalStoreMock({ meta: mockData, callback: responseCallback });

		const { getByRole } = render(WeightTargetModal);
		await fireEvent.input(getByRole('spinbutton', { name: 'Starting weight kg' }), {
			target: { value: 80 }
		});
		await fireEvent.input(getByRole('spinbutton', { name: 'Target weight kg' }), {
			target: { value: 70 }
		});
		await fireEvent.click(getByRole('button', { name: 'Submit' }));
		await tick();

		expect(responseCallback).toHaveBeenCalledTimes(1);

		const expectedDetail = {
			weightTarget: {
				added: getDateAsStr(new Date()),
				startDate: getDateAsStr(new Date()),
				initialWeight: '80',
				endDate: getDateAsStr(addYears(new Date(), 1)),
				targetWeight: '70'
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
				initialWeight: '80',
				targetWeight: '70'
			}
		};

		updateModalStoreMock({ meta: mockData });

		const { getByRole } = render(WeightTargetModal);

		expect(getByRole('spinbutton', { name: 'Starting weight kg' }).value).toBe(
			mockData.currentTarget.initialWeight
		);
		expect(getByRole('spinbutton', { name: 'Target weight kg' }).value).toBe(
			mockData.currentTarget.targetWeight
		);
	});

	/**
	 * Given a component only receives the 'currentWeight' from meta of $modalStore[0]
	 * - Render modal
	 * - assert initialWeight is prefilled with currentWeight
	 */
	it('should accept currentWeight from props', async () => {
		const mockData = {
			currentWeight: 75
		};

		updateModalStoreMock({ meta: mockData });

		const { getByRole } = render(WeightTargetModal);

		expect(getByRole('spinbutton', { name: 'Starting weight kg' }).value).toBe('75');
		expect(getByRole('spinbutton', { name: 'Target weight kg' }).value).toBeFalsy();
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

		const { getAllByRole } = render(WeightTargetModal);

		await fireEvent.click(getAllByRole('button', { name: 'Cancel' })[0]);
		await tick();

		expect(responseCallback).toHaveBeenCalledTimes(1);

		const expectedDetail = { cancelled: true };

		expect(responseCallback).toHaveBeenCalledWith(expectedDetail);
	});
});
