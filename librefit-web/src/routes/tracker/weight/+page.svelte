<script>
	import {getToastStore, RadioGroup, RadioItem} from '@skeletonlabs/skeleton';
	import {createWeightChart, createWeightChartDataset, DataViews, enumKeys} from '$lib/util.js';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {Line} from 'svelte-chartjs';
	import {Chart, registerables} from 'chart.js';
	import {showToastError, showToastSuccess} from '$lib/toast.js';
	import * as weight_crud from '$lib/api/weight-rest.js';
	import {getContext} from 'svelte';

	Chart.register(...registerables);

	const toastStore = getToastStore();

	export let filter = DataViews.Month;

	/** @type import('./$types').PageData */
	export let data;

	const today = new Date();

	let entries;
	let chartData, chartOptions;

	const currentGoal = getContext('currentGoal');
	const lastEntry = getContext('lastWeight')

	const loadEntriesFiltered = async () => {
		await fetch(`/tracker/weight?filter=${filter}`, {
			method: 'GET'
		}).then(async (result) => {
			paint(await result.json());
		});
	}

	const paint = (entries) => {
		const noNaN = entries.map(entry => entry.amount);

		if (noNaN.length > 0) {
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
	}

	$: if (data && data.entries) {
		entries = data.entries;

		paint(data.entries);
	}

	const add = (e) => {
		weight_crud.add(e, reload, toastStore, '/tracker/weight');
	}

	const update = (e) => {
		weight_crud.update(e, reload, toastStore, '/tracker/weight');
	}

	const remove = (e) => {
		weight_crud.remove(e, reload, toastStore, '/tracker/weight');
	}

	const reload = (result) => {
		if (result.status === 200 || result.status === 201) {
			fetch(`/tracker/weight?filter=${filter}`, {
				method: 'GET'
			}).then(async (response) => {
				paint(await response.json());
			}).catch(e => showToastError(toastStore, e))
		} else {
			throw Error(result.status)
		}
	}

	const updateGoal = (e) => {
		console.log(e);

		let goal = e.detail.goal;

		if (!currentGoal) {
			fetch('/tracker/weight', {
				method: 'POST',
				body: JSON.stringify({
					goal: goal
				}),
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(async (response) => {
				currentGoal.set(response.json());
			}).catch(e => showToastError(toastStore, e));
		} else {
			fetch('/tracker/weight', {
				method: 'PUT',
				body: JSON.stringify({
					goal: goal
				})
			}).then(async (response) => currentGoal.set(await response.json()))
			.catch(e => showToastError(toastStore, e))
		}
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
					<RadioItem bind:group={filter} name="justify" value={DataViews[dataView]} on:change={loadEntriesFiltered}
					>{dataView}</RadioItem
					>
				{/each}
			</RadioGroup>

			{#if chartData}
				<Line data={chartData} options={chartOptions} />
			{/if}

			<WeightTracker {entries}
						   bind:lastEntry={$lastEntry}
						   bind:goal={$currentGoal}
						   on:addWeight={add}
						   on:updateWeight={update}
						   on:deleteWeight={remove}
						   on:updateGoal={updateGoal}
			/>
		</div>
	</div>
</section>