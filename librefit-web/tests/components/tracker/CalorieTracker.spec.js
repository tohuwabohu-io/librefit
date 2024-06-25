import { render, fireEvent, cleanup, screen } from '@testing-library/svelte';
import { afterEach, expect, describe, it, vi } from 'vitest';
import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
import { tick } from 'svelte';
import * as addMultiEvent from 'chart.js/helpers';

/**
 * @vitest-environment jsdom
 */
describe('CalorieTracker.svelte component', () => {
	afterEach(() => cleanup());

	it('should render a pristine component and add two entries', async () => {
		const mockData = {
			categories: [
				{ shortvalue: 'b', longvalue: 'Breakfast', visible: true },
				{ shortvalue: 'l', longvalue: 'Lunch', visible: true },
				{ shortvalue: 'd', longvalue: 'Dinner', visible: true }
			],
			entries: [
				{
					// there must always be one entry prepended or otherwise the component won't have any controls
					added: '2022-02-02',
					sequence: undefined,
					category: 'l'
				}
			]
		};

		const { component } = render(CalorieTracker, { ...mockData });

		let addEvent;
		const addMock = vi.fn((event) => {
			addEvent = event.detail;
			mockData.entries.unshift({
				added: '2022-02-02',
				sequence: undefined,
				category: 'd'
			});

			// add new entry -> list expands
			component.$set({ entries: mockData.entries });
		});

		await component.$on('addCalories', addMock);

		// for now there should be only one of those components each
		const amountInput = screen.getByRole('spinbutton', { name: 'amount' });
		const categoryCombobox = screen.getByRole('combobox', { name: 'category' });

		expect(amountInput.placeholder).toEqual('Amount...');
		expect(amountInput.value).toBeFalsy();
		expect(categoryCombobox.value).toStrictEqual('l');

		// change amount + category and click 'add'
		await fireEvent.input(amountInput, { target: { value: 100 } });
		await fireEvent.change(categoryCombobox, { target: { value: 'd' } });

		expect(amountInput.value).toStrictEqual('100');
		expect(categoryCombobox.value).toStrictEqual('d');

		await fireEvent.click(screen.getByRole('button', { name: 'add' }));
		await tick();

		expect(addMock).toHaveBeenCalledTimes(1);

		expect(addEvent).toEqual({
			callback: expect.any(Function),
			target: undefined,
			dateStr: '2022-02-02',
			category: 'd',
			value: 100
		});

		expect(mockData.entries.length).toStrictEqual(2);

		await fireEvent.input(screen.getAllByRole('spinbutton', { name: 'amount' })[1], {
			target: { value: 700 }
		});
		await fireEvent.change(screen.getAllByRole('combobox', { name: 'category' })[1], {
			target: { value: 'b' }
		});
		await fireEvent.click(screen.getAllByRole('button', { name: 'add' })[1]);
		await tick();

		expect(addMock).toHaveBeenCalledTimes(2);
		expect(addEvent).toEqual({
			callback: expect.any(Function),
			target: undefined,
			dateStr: '2022-02-02',
			category: 'b',
			value: 700
		});
	});
});
