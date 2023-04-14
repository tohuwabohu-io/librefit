<script lang="ts">
	import {RadioGroup, RadioItem, toastStore} from '@skeletonlabs/skeleton';
	import {DataViews, enumKeys, getDateAsStr, weakEntityEquals, createWeightChart, createWeightChartDataset} from '$lib/util';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {Configuration, WeightTrackerEntry, WeightTrackerResourceApi} from 'librefit-api/rest';
	import {PUBLIC_API_BASE_PATH} from '$env/static/public';
	import {onMount} from 'svelte';
	import {Line} from 'svelte-chartjs';
	import { Chart, registerables } from 'chart.js';

	Chart.register(...registerables);

	let filter = DataViews.Year;
	const today = new Date();

	let entries: Array<WeightTrackerEntry> = [];
	let firstTime = false;
	let initialAmount = 0;
	let chartData;

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
				paint();
			}).catch(handleLoadError);
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

					paint();
				} else {
					api.findLastWeightTrackerEntry({
						userId : 1
					}).then((entry) => {
						if (entry) {
							entries = [ entry ];
						}
					}).catch((error) => {
						if (!error.response || error.response.status !== 404) {
							handleLoadError(error);
						} else {
							firstTime = true;
						}
					})
				}
			}).catch(handleLoadError)
		}
	}

	const paint = () => {
		const chart = createWeightChart(filter, today, entries);
		const dataset = createWeightChartDataset(chart.data);

		console.log(chart.legend);
		console.log(dataset);

		chartData = {
			labels: chart.legend,
			datasets: [dataset]
		}
	}

	const add = (e) => {
		const newEntry: WeightTrackerEntry = {
			userId: 1,
			id: e.detail.sequence,
			added: e.detail.todayDateStr,
			amount: e.detail.value
		}

		api.createWeightTrackerEntry({
			weightTrackerEntry: newEntry
		}).then((result: WeightTrackerEntry) => {
			entries.push(result);
			entries = entries;

			initialAmount = 0;

			toastStore.trigger({
				message: 'Weight added successfully!',
				background: 'variant-filled-primary',
				autohide: true
			})
		}).catch(handleLoadError)
	}

	const update = (e) => {
		api.readWeightTrackerEntry({
			userId: 1,
			id: e.detail.sequence,
			date: e.detail.date
		}).then((entry: WeightTrackerEntry) => {
			entry.amount = e.detail.value;

			api.updateWeightTrackerEntry({
				weightTrackerEntry: entry
			}).then(_ => {
				toastStore.trigger({
					message: 'Update successful.',
					background: 'variant-filled-primary',
					autohide: true
				})
			}).catch(handleLoadError)
		}).catch(handleLoadError)
	}

	const remove = (e) => {
		api.deleteWeightTrackerEntry({
			userId: 1,
			id: e.detail.sequence,
			date: e.detail.date
		}).then(_ => {
			entries = entries.filter((entry: WeightTrackerEntry) => !weakEntityEquals(entry, {
				id: e.detail.id,
				added: e.detail.date,
				userId: 1
			}));
		}).catch(handleLoadError)
	}

	const handleLoadError = (err) => {
		console.error(err);

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

			<Line data={chartData} options={{responsive: true}} />

			<WeightTracker bind:entries={entries} bind:firstTime={firstTime} bind:initialAmount={initialAmount}
				on:addWeight={add} on:updateWeight={update} on:deleteWeight={remove}/>
		</div>
	</div>
</section>
