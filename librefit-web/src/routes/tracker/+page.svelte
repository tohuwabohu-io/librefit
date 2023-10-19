<script>
	import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';
	import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/util';
	import {handleApiError, showToastSuccess} from '$lib/toast.js';
	import TrackerRadial from '$lib/components/TrackerRadial.svelte';
	import TrackerInput from '$lib/components/TrackerInput.svelte';
	import {Category} from '$lib/api/model.js';

	let today = new Date();
	let todayStr = getDateAsStr(today);

	export let data;

	let datesToEntries = {};

	$: datesToEntries;
	$: if (data && data.entryToday) {
		datesToEntries[todayStr] = data.entryToday;
	}

	const categories = Object.keys(Category).map((key) => {
		return {
			label: key,
			value: Category[key]
		};
	});

	const addEntry = async (e) => {
		let id;

		if (await datesToEntries[todayStr]) {
			id = Math.max(...(await datesToEntries[todayStr]).map(entry => entry.id).filter(id => id !== undefined)) + 1;
		} else {
			id = Math.max(...data.entryToday.map(entry => entry.id).filter(id => id !== undefined)) + 1;
		}

		/** @type {CalorieTrackerEntry} */
		const newEntry = {
			id: id,
			added: e.detail.date,
			amount: e.detail.value,
			category: e.detail.category
		};

		fetch('/tracker', {
			method: 'POST',
			body: JSON.stringify(newEntry)
		}).then(_ => {
			loadEntries(newEntry.added);
		}).catch(handleApiError);
	};

	const updateEntry = (e) => {
		/** @type {CalorieTrackerEntry} */
		const entry = {
			id: e.detail.sequence,
			added: e.detail.date,
			amount: e.detail.value,
			category: e.detail.category
		};

		fetch('/tracker', {
			method: 'PUT',
			body: JSON.stringify(entry)
		}).then(_ => {
			loadEntries(entry.added);
			showToastSuccess('Entry updated successfully!')
		}).catch(handleApiError)
	};

	const deleteEntry = (e) => {
		console.log(e);

		fetch(`/tracker?sequence=${e.detail.sequence}&added=${e.detail.date}`, {
			method: 'DELETE',
		}).then((response) => {
			if (response.status === 200) {
				loadEntries(e.detail.date);
			} else {
				throw Error(response.status);
			}
		}).catch(handleApiError)
	};

	const loadEntries = async (added) => {
		const response = await fetch(`/tracker?added=${added}`, {method: 'GET'});
		const result = response.json();

		datesToEntries[added] = await result;

		if (response.ok) {
			return result;
		} else {
			throw new Error(result);
		}
	}
</script>

<svelte:head>
	<title>LibreFit - Calorie Tracker</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		{#if data.availableDates}
			{#each [...data.availableDates] as dateStr}
			<Accordion class="variant-ghost-surface rounded-xl">
				<AccordionItem id={dateStr} open={dateStr === todayStr} on:toggle|once={loadEntries(dateStr)}>
					<svelte:fragment slot="summary">
						{convertDateStrToDisplayDateStr(dateStr)}
					</svelte:fragment>
					<svelte:fragment slot="content">
						<div class="flex gap-4 justify-between">
							{#if dateStr === todayStr && data.entryToday && !datesToEntries[dateStr]}
								<TrackerRadial entries={data.entryToday.map(e => e.amount)} />
								<div class="flex flex-col grow gap-4">
									{#each data.entryToday as entry}
										<TrackerInput {categories}
												value={entry.amount}
												{dateStr}
												id={entry.id}
												on:add={addEntry}
												on:update={updateEntry}
												on:remove={deleteEntry}
												unit={'kcal'}
										/>
									{/each}
								</div>
							{:else}
								{#await datesToEntries[dateStr]}
									<p>... loading</p>
								{:then entries}
									{#if entries}
									<TrackerRadial entries={entries.map(e => e.amount)} />
									<div class="flex flex-col grow gap-4">
										{#each entries as entry}
											<TrackerInput {categories}
													value={entry.amount}
													dateStr={entry.added}
													id={entry.id}
												    category={entry.category}
													on:add={addEntry}
													on:update={updateEntry}
													on:remove={deleteEntry}
												    existing={entry.id !== undefined}
													disabled={entry.id !== undefined}
													unit={'kcal'}
											/>
										{/each}
									</div>
									{/if}
								{:catch error}
									<p>{error}</p>
								{/await}
							{/if}
						</div>
					</svelte:fragment>
				</AccordionItem>
			</Accordion>
			{/each}
		{:else}
			{data.error}
		{/if}
	</div>
</section>
