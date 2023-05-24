<script lang="ts">
	import {RadioGroup, RadioItem, toastStore} from '@skeletonlabs/skeleton';
	import {DataViews, enumKeys, getDateAsStr, weakEntityEquals, createWeightChart, createWeightChartDataset} from '$lib/util';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {
		WeightTrackerEntry,
		WeightTrackerResourceApi,
		GoalsResourceApi,
		Goal
	} from 'librefit-api/rest';
	import {onMount} from 'svelte';
	import {Line} from 'svelte-chartjs';
	import { Chart, registerables } from 'chart.js';
	import {JWT_CONFIG} from '../../lib/api/Config';

	Chart.register(...registerables);

	let filter = DataViews.Month;
	const today = new Date();

	let entries: Array<WeightTrackerEntry> = [];
	let lastEntry;
	let chartData, chartOptions;
	let currentGoal;

	const weightApi = new WeightTrackerResourceApi(JWT_CONFIG);
	const goalApi = new GoalsResourceApi(JWT_CONFIG);

	const loadEntries = async () => {
		await weightApi.findLastWeightTrackerEntry().catch((error) => {
			if (!error.response || error.response.status !== 404) {
				handleApiError(error);
			}
		}).then((entry) => {
			lastEntry = entry;

			if (filter === DataViews.Today) {
				weightApi.listWeightTrackerEntries({
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

				weightApi.listWeightTrackerEntriesRange({
					dateFrom: getDateAsStr(fromDate),
					dateTo: getDateAsStr(toDate)
				}).then((result: Array<WeightTrackerEntry>) => {
					entries = result;

					paint();
				}).catch(handleApiError)
			}
		});

		await goalApi.findLastGoal().then((result: Goal) => currentGoal = result).catch((error) => {
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
			id: e.detail.sequence,
			added: e.detail.todayDateStr,
			amount: e.detail.value
		}

		weightApi.createWeightTrackerEntry({
			weightTrackerEntry: newEntry
		}).then((result: WeightTrackerEntry) => {
			entries.push(result);
			entries = entries;

			showToastSuccess('Update successful.');
			paint();
		}).catch(handleApiError)
	}

	const update = (e) => {
		weightApi.readWeightTrackerEntry({
			id: e.detail.sequence,
			date: e.detail.date
		}).then((entry: WeightTrackerEntry) => {
			entry.amount = e.detail.value;

			weightApi.updateWeightTrackerEntry({
				weightTrackerEntry: entry
			}).then(_ => {
				showToastSuccess('Update successful.');

				paint();
			}).catch(handleApiError)
		}).catch(handleApiError)
	}

	const remove = (e) => {
		weightApi.deleteWeightTrackerEntry({
			id: e.detail.sequence,
			date: e.detail.date
		}).then(_ => {
			entries = entries.filter((entry: WeightTrackerEntry) => !weakEntityEquals(entry, {
				id: e.detail.sequence,
				added: e.detail.date,
			}));

			showToastSuccess('Deletion successful.');

			paint();
		}).catch(handleApiError)
	}

	const updateGoal = (e) => {
		console.log(e);

		let goal: Goal = e.detail.goal as Goal;

		if (!currentGoal) {
			goalApi.createGoal({goal}).then((response) => {
				currentGoal = response;
			}).catch(handleApiError)
		} else {
			goalApi.updateGoal({goal}).then(_ => {
				goalApi.readGoal({
					date: currentGoal.added,
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

	const showToastSuccess = (toastMessage: String) => {
		toastStore.trigger({
			message: toastMessage,
			background: 'variant-filled-primary',
			autohide: true
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