import { cleanup, fireEvent, render, screen } from '@testing-library/svelte';
import { afterEach, describe, expect, it, vi } from 'vitest';
import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
import { tick } from 'svelte';

/**
 * @vitest-environment jsdom
 */
describe('CalorieTracker.svelte component', () => {
	afterEach(() => cleanup());

	/*
	 * Expectations:
	 *  - render an empty component with one empty entry (number of TrackerInput components: 1)
	 *  - change default data, click 'add'
	 *  - addCalories must have been triggered once
	 *  - addCalories must fire event with changed data
	 *  - add a new empty entry on top (number of TrackerInput components: 2)
	 *  - repeat process
	 *  - number of TrackerInput components must equal to 3
	 */
	it('should render a pristine component, add two entries and grow', async () => {
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

			// component won't add element by itself as the page would reload the whole dataset
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

		expect(screen.getAllByRole('spinbutton', { name: 'amount' }).length).toStrictEqual(3);
	});

	/*
	 * Expectations
	 * 	- render component with two entries filled and one empty (number of TrackerInput components: 3)
	 *  - for the 2nd entry (lunch), click 'delete' and confirm
	 *  - deleteCalories must have been triggered once
	 *  - deleteCalories must fire event with entry data that shall be deleted
	 *  - number of TrackerInput components must equal to 2
	 *  - repeat process for dinner (the new 2nd entry)
	 *  - number of TrackerInput components must equal to 1
	 */
	it('should render a filled component, remove two entries and shrink', async () => {
		const lunch = { added: '2022-02-02', amount: 500, sequence: 1, category: 'l' };
		const dinner = { added: '2022-02-02', amount: 500, sequence: 2, category: 'd' };
		const empty = { added: '2022-02-02', sequence: undefined, category: 'l' };

		const mockData = {
			categories: [
				{ shortvalue: 'b', longvalue: 'Breakfast', visible: true },
				{ shortvalue: 'l', longvalue: 'Lunch', visible: true },
				{ shortvalue: 'd', longvalue: 'Dinner', visible: true }
			],
			entries: [empty, lunch, dinner]
		};

		const { component } = render(CalorieTracker, { ...mockData });

		let deleteEvent;
		const deleteMock = vi.fn((event) => {
			deleteEvent = event.detail;

			// component won't remove element by itself as the page would reload the whole dataset
			const index = mockData.entries.findIndex((entry) => entry.sequence === event.detail.sequence);
			mockData.entries.splice(index, 1);
			component.$set({ entries: mockData.entries });
		});

		await component.$on('deleteCalories', deleteMock);

		// the empty entry does not have a delete button, so the index shifts by -1
		const deleteButton = screen.getAllByRole('button', { name: 'delete' })[0];
		await fireEvent.click(deleteButton);

		const confirmButton = screen.getByRole('button', { name: 'confirm' });
		await fireEvent.click(confirmButton);
		await tick();

		expect(deleteMock).toHaveBeenCalledTimes(1);
		expect(deleteEvent).toEqual({
			callback: expect.any(Function),
			target: confirmButton,
			sequence: 1,
			dateStr: '2022-02-02'
		});

		expect(mockData.entries.length).toStrictEqual(2);

		await fireEvent.click(deleteButton);
		await fireEvent.click(confirmButton);
		await tick();

		expect(deleteMock).toHaveBeenCalledTimes(2);
		expect(deleteEvent).toEqual({
			callback: expect.any(Function),
			target: confirmButton,
			sequence: 2,
			dateStr: '2022-02-02'
		});

		expect(screen.getAllByRole('spinbutton', { name: 'amount' }).length).toStrictEqual(1);
	});

	/*
	 * Expectations
	 *  - render component with one entry filled and one empty (number of TrackerInput components: 2)
	 *  - for the filled entry click 'update' and confirm
	 *  - updateCalories must have been triggered once
	 *  - updateCalories must fire event with changed entry
	 */
	it('should render a filled component and update one entry', async () => {
		const breakfast = { added: '2022-02-02', amount: 500, sequence: 1, category: 'b' };
		const empty = { added: '2022-02-02', sequence: undefined, category: 'l' };

		const mockData = {
			categories: [
				{ shortvalue: 'b', longvalue: 'Breakfast', visible: true },
				{ shortvalue: 'l', longvalue: 'Lunch', visible: true },
				{ shortvalue: 'd', longvalue: 'Dinner', visible: true }
			],
			entries: [empty, breakfast]
		};

		const { component } = render(CalorieTracker, { ...mockData });

		let updateEvent;
		const updateMock = vi.fn((event) => {
			updateEvent = event.detail;
			mockData.entries[event.detail.sequence] = event.detail;
			component.$set({ entries: mockData.entries });
		});

		await component.$on('updateCalories', updateMock);

		await fireEvent.click(screen.getByRole('button', { name: 'edit' }));
		await fireEvent.input(screen.getAllByRole('spinbutton', { name: 'amount' })[1], {
			target: { value: 600 }
		});
		await fireEvent.click(screen.getByRole('button', { name: 'confirm' }));
		await tick();

		expect(updateMock).toHaveBeenCalledTimes(1);
		expect(updateEvent).toEqual({
			callback: expect.any(Function),
			sequence: 1,
			dateStr: '2022-02-02',
			category: 'b',
			value: 600
		});
	});
});
