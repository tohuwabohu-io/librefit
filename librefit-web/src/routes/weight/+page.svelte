<script lang="ts">
	import {RadioGroup, RadioItem, toastStore} from '@skeletonlabs/skeleton';
	import {DataViews, enumKeys, getDateAsStr} from '$lib/util';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {Configuration, WeightTrackerEntry, WeightTrackerResourceApi} from 'librefit-api/rest';
	import {PUBLIC_API_BASE_PATH} from '$env/static/public';
	import {onMount} from 'svelte';

	let filter = DataViews.Month;
	const today = new Date();

	let entries: Array<WeightTrackerEntry> = [];
	let firstTime = false;

	const api = new WeightTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

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
						} else {
							firstTime = true;
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

	onMount(async () => {
		await loadEntries()
	});
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

			<WeightTracker bind:entries={entries} bind:firstTime={firstTime} />
		</div>
	</div>
</section>
