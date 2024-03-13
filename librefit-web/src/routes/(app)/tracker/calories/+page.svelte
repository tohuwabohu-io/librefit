<script>
	import {Accordion, AccordionItem, getToastStore} from '@skeletonlabs/skeleton';
	import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/util.js';
	import {Category} from '$lib/api/model.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import * as ct_crud from '$lib/api/calories-rest.js';

	let today = new Date();
	let todayStr = getDateAsStr(today);

	const toastStore = getToastStore();

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
		ct_crud.addEntry(e, loadEntries, toastStore, '/tracker/calories');
	};

	const updateEntry = (e) => {
		ct_crud.updateEntry(e, loadEntries, toastStore, '/tracker/calories');
	};

	const deleteEntry = (e) => {
		ct_crud.deleteEntry(e, loadEntries, toastStore, '/tracker/calories');
	};

	const loadEntries = async (added) => {
		const response = await fetch(`/tracker/calories?added=${added}`, {method: 'GET'});
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
						<div class="flex lg:flex-row flex-col gap-4 grow">
							{#if dateStr === todayStr && data.entryToday && !datesToEntries[dateStr]}
								<CalorieTracker entries={data.entryToday} {categories}
									on:addCalories={addEntry}
									on:updateCalories={updateEntry}
									on:deleteCalories={deleteEntry}
								/>
							{:else}
								{#await datesToEntries[dateStr]}
									<p>... loading</p>
								{:then entries}
									{#if entries}
										<CalorieTracker {entries} {categories}
											on:addCalories={addEntry}
											on:updateCalories={updateEntry}
											on:deleteCalories={deleteEntry}
										/>
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