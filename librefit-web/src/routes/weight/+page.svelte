<script lang="ts">
	import { RadioGroup, RadioItem } from '@skeletonlabs/skeleton';
	import { Configuration, WeightTrackerResourceApi, WeightTrackerEntry } from 'librefit-api/rest';
	import { DataViews, enumKeys, getDateAsStr } from '$lib/util';
	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import { onMount } from 'svelte';
	import { getDisplayDateAsStr } from '$lib/util';
	import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
	import NewSection from '$lib/assets/icons/new-section.svg?component';
	import { toastStore } from '@skeletonlabs/skeleton';
	import TrackerInput from '$lib/components/TrackerInput.svelte';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';

	let filter = DataViews.Month;

	const api = new WeightTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const today = new Date();

	let entries: Array<WeightTrackerEntry> = [];

	onMount(async () => {
		await loadEntries()
	});

    const loadEntries = async () => {
        if (filter === DataViews.Today) {
            return await api.listWeightTrackerEntries({
				userId: 2,
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
				userId: 2,
				dateFrom: getDateAsStr(fromDate),
				dateTo: getDateAsStr(toDate)
			}).then((result: Array<WeightTrackerEntry>) => {
				if (result.length > 0) {
					entries = result;
				} else {
					api.findLastWeightTrackerEntry({
						userId : 2
					}).then((entry) => {
						if (entry) {
							entries = [ entry ];
						}
					}).catch((error) => {
						if (!error.response || error.response.status !== 404) {
							handleLoadError();
						}
					})
				}
			}).catch(handleLoadError)
		}
    }

	const handleLoadError = () => {
		toastStore.trigger({
			message: 'An error occured. Please try again later.',
			background: 'variant-filled-warning',
			autohide: false
		})
	}
</script>

<svelte:head>
	<title>LibreFit - Weight Tracker</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<div class="flex flex-col gap-4">
			<RadioGroup>
				{#each enumKeys(DataViews) as dataView}
					<RadioItem bind:group={filter} name="justify" value={DataViews[dataView]} on:change={loadEntries}
					>{dataView}</RadioItem
					>
				{/each}
			</RadioGroup>

			{#if entries.length <= 0}
				<div class="flex flex-row gap-2">
					<NoScale width={200} height={200} />
					<div>
						<p>
							Seems like you have not tracked anything so far. Today is the best day to start!
						</p>
						<button class="btn">
							<span>
								<NewSection class="w-32 h-32" />
							</span>
						</button>
					</div>
				</div>
			{/if}

			<WeightTracker bind:trackerEntries={entries} />
<!--
			{#each entries as entry}
				<div class="flex flex-row gap-2">
					<Scale width={200} height={200} />
					<div class="input-group input-group-divider grid-cols-[auto_1fr_auto]">
						<div class="input-group-shim">kg</div>
						<input type="number" placeholder="Amount..." bind:value={entry.amount} />
					</div>
				</div>
			{/each}
		</div>
-->
	</div>
</section>
