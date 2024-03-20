<script>
	import {Accordion, AccordionItem, getToastStore} from '@skeletonlabs/skeleton';
	import {Category} from '$lib/api/model.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import * as ct_crud from '$lib/api/tracker.js';
	import {validateAmount} from '$lib/validation.js';
	import {showToastError, showToastSuccess, showToastWarning} from '$lib/toast.js';
	import {getContext} from 'svelte';
    import {getCategoryValueAsKey} from '$lib/enum.js';
    import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/date.js';
	import {goto} from '$app/navigation';

	let today = new Date();
	let todayStr = getDateAsStr(today);

	const toastStore = getToastStore();
	const indicator = getContext('indicator');
	const user = getContext('user');

	if (!$user) goto('/');

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

	const addEntry = async (event) => {
		const amountMessage = validateAmount(event.detail.value);

		if (!amountMessage) {
			$indicator = $indicator.start(event.detail.target);

			await ct_crud.addCalories(event).then(async response => {
				event.detail.callback();

				datesToEntries[event.detail.date] = await response;

				showToastSuccess(
					toastStore,
					`Successfully added ${
						event.detail.category !== Category.Unset
							? getCategoryValueAsKey(event.detail.category)
							: 'calories'
					}.`
				);

			}).catch((e) => {
				showToastError(toastStore, e);
				event.detail.callback(true);
			}).finally(() => {$indicator = $indicator.finish()})
		} else {
			showToastWarning(toastStore, amountMessage);
			event.detail.callback();
		}
	};

	const updateEntry = async (event) => {
		const amountMessage = validateAmount(event.detail.value);

		if (!amountMessage) {
			$indicator = $indicator.start(event.detail.target);

			await ct_crud.updateCalories(event).then(async response => {
				event.detail.callback();

				datesToEntries[event.detail.date] = await response;

				showToastSuccess(
					toastStore,
					`Successfully updated ${
							event.detail.category !== Category.Unset ? getCategoryValueAsKey(event.detail.category) : 'calories'
					}`
				);
			}).catch((e) => {
				showToastError(toastStore, e);
				event.detail.callback(true);
			}).finally(() => $indicator = $indicator.finish());
		} else {
			showToastWarning(toastStore, amountMessage);
			event.detail.callback(true);
		}
	};

	const deleteEntry = async (event) => {
		$indicator = $indicator.start(event.detail.target);

		await ct_crud.deleteCalories(event).then(async response => {
			event.detail.callback();

			datesToEntries[event.detail.date] = await response;

			showToastSuccess(toastStore, `Deletion successful.`);
		}).catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback(true);
		}).finally(() => {$indicator = $indicator.finish()})
	};

	const loadEntries = async (added) => {
		$indicator = $indicator.start();

		await ct_crud.listCaloriesForDate(added).then(async response => {
			datesToEntries[added] = await response;
		}).catch((e) => { showToastError(toastStore, e) }).finally(() => {$indicator = $indicator.finish()})
	}
</script>

<svelte:head>
	<title>LibreFit - Calorie Tracker</title>
</svelte:head>

{#if $user}
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
{/if}