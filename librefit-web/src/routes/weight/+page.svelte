<script lang="ts">
	import { RadioGroup, RadioItem } from '@skeletonlabs/skeleton';
	import { Configuration, WeightTrackerResourceApi, WeightTrackerEntry } from 'librefit-api/rest';
	import { DataViews, enumKeys, getDateAsStr } from '$lib/util';
	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import { onMount } from 'svelte';

	let filter = DataViews.Month;

	const api = new WeightTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const today = new Date();

	let entries: Array<WeightTrackerEntry> = [];

	onMount(async () => { await loadEntries() });

    const loadEntries = async () => {
        if (filter === DataViews.Today) {
            return await api.listWeightTrackerEntries({
				userId: 1,
				date: getDateAsStr(today)
			}).then((result: Array<WeightTrackerEntry>) => {
				entries = result;
			}).catch(console.log);
        } else {
			const toDate = today;
			const fromDate = new Date();

			switch (filter) {
				case DataViews.Week: fromDate.setDate(fromDate.getDate() -7); break;
				case DataViews.Month: fromDate.setMonth(fromDate.getMonth() - 1); break;
				case DataViews.Year: fromDate.setFullYear(fromDate.getFullYear() - 1); break;
				default: break;
			}

			return await api.listWeightTrackerEntriesRange({
				userId: 1,
				dateFrom: getDateAsStr(fromDate),
				dateTo: getDateAsStr(toDate)
			}).then((result: Array<WeightTrackerEntry>) => {
				entries = result;
			}).catch(console.log)
		}
    }
</script>

<svelte:head>
	<title>LibreFit - Weight Tracker</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<RadioGroup>
			{#each enumKeys(DataViews) as dataView}
				<RadioItem bind:group={filter} name="justify" value={DataViews[dataView]} on:change={loadEntries}
					>{dataView}</RadioItem
				>
			{/each}
		</RadioGroup>
	</div>

	{#each entries as entry}
		<div class="input-group input-group-divider grid-cols-[auto_1fr_auto]">
			<div class="input-group-shim">kg</div>
			<input type="number" placeholder="Amount..." bind:value={entry.amount} />
		</div>
	{/each}
</section>
