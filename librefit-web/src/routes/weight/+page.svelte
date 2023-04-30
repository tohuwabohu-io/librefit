<script lang="ts">
	import {RadioGroup, RadioItem, toastStore} from '@skeletonlabs/skeleton';
	import {DataViews, enumKeys, getDateAsStr, weakEntityEquals, createWeightChart, createWeightChartDataset} from '$lib/util';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {
		Configuration,
		WeightTrackerEntry,
		WeightTrackerResourceApi,
		GoalsResourceApi,
		Goal
	} from 'librefit-api/rest';
	import {PUBLIC_API_BASE_PATH} from '$env/static/public';
	import {onMount} from 'svelte';
	import {Line} from 'svelte-chartjs';
	import { Chart, registerables } from 'chart.js';

	Chart.register(...registerables);

	let filter = DataViews.Month;
	const today = new Date();

	let entries: Array<WeightTrackerEntry> = [];
	let lastEntry;
	let chartData, chartOptions;
	let currentGoal;

	const api = new WeightTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const goalApi = new GoalsResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const loadEntries = async () => {
		await api.findLastWeightTrackerEntry({
			userId: 1
		}).catch((error) => {
			if (!error.response || error.response.status !== 404) {
				handleApiError(error);
			}
		}).then((entry) => {
			lastEntry = entry;

			if (filter === DataViews.Today) {
				api.listWeightTrackerEntries({
					userId: 1,
					date: getDateAsStr(today)
				}).then((result: Array<WeightTrackerEntry>) => {
					entries = result;

					paint();
				}).catch(handleApiError);
			} else {
				const toDate = today;
				const fromDate = new Date();

				switch (filter) {
					case DataViews.Week: fromDate.setDate(fromDate.getDate() -7); break;
					case DataViews.Month: fromDate.setMonth(fromDate.getMonth() - 1); break;
					case DataViews.Year: fromDate.setFullYear(fromDate.getFullYear() - 1); break;
					default: break;
				}

				api.listWeightTrackerEntriesRange({
					userId: 1,
					dateFrom: getDateAsStr(fromDate),
					dateTo: getDateAsStr(toDate)
				}).then((result: Array<WeightTrackerEntry>) => {
					entries = result;

					paint();
				}).catch(handleApiError)
			}
		});

		await goalApi.findLastGoal({
			userId: 1
		}).then((result: Goal) => currentGoal = result).catch((error) => {
				if (!error.response || error.response.status !== 404) {
					handleApiError(error);
				}
			}
		)
	}

	const paint = () => {
		const noNaN = entries.map(entry => entry.amount);

		if (filter !== DataViews.Today) {
			const chart = createWeightChart(filter, today, entries);
			const dataset = createWeightChartDataset(chart.data);

			chartData = {
				labels: chart.legend,
				datasets: [dataset]
			}
		} else {
			chartData = null;
		}

		chartOptions = {
			responsive: true,
			scales: {
				y: {
					suggestedMin: Math.min(...noNaN) - 2.5,
					suggestedMax: Math.max(...noNaN) + 2.5
				}
			}
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

			toastStore.trigger({
				message: 'Weight added successfully!',
				background: 'variant-filled-primary',
				autohide: true
			});

			paint();
		}).catch(handleApiError)
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

				paint();
			}).catch(handleApiError)
		}).catch(handleApiError)
	}

	const remove = (e) => {
		api.deleteWeightTrackerEntry({
			userId: 1,
			id: e.detail.sequence,
			date: e.detail.date
		}).then(_ => {
			entries = entries.filter((entry: WeightTrackerEntry) => !weakEntityEquals(entry, {
				id: e.detail.sequence,
				added: e.detail.date,
				userId: 1
			}));

			paint();
		}).catch(handleApiError)
	}

	const updateGoal = (e) => {
		console.log(e);

		let goal: Goal = e.detail.goal as Goal;
		goal.userId = 1;

		if (!currentGoal) {
			goalApi.createGoal({goal}).then((response) => {
				currentGoal = response;
			}).catch(handleApiError)
		} else {
			goalApi.updateGoal({goal}).then(_ => {
				goalApi.readGoal({
					date: currentGoal.added,
					userId: currentGoal.userId,
					id: currentGoal.id
				}).then((response) => currentGoal = response).catch(handleApiError)
			}).catch(handleApiError)
		}
	}

	const handleApiError = (err) => {
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


			{#if chartData}
				<Line data={chartData} options={chartOptions} />
			{/if}
			<WeightTracker bind:entries={entries}
						   {lastEntry}
						   bind:goal={currentGoal}
						   on:addWeight={add}
						   on:updateWeight={update}
						   on:deleteWeight={remove}
						   on:updateGoal={updateGoal}
			/>
		</div>
	</div>
</section>
