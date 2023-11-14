<script>
	import {getToastStore, RadioGroup, RadioItem} from '@skeletonlabs/skeleton';
	import {createWeightChart, createWeightChartDataset, DataViews, enumKeys} from '$lib/util.js';
	import {Line} from 'svelte-chartjs';
	import {Chart, registerables} from 'chart.js';
	import {showToastError, showToastSuccess} from '$lib/toast.js';
	import * as weight_crud from '$lib/api/weight-rest.js';
	import {getContext} from 'svelte';
	import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';

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
			const chart = createWeightChart(filter, today, entries);
			const dataset = createWeightChartDataset(chart.data);

			chartData = {
				labels: chart.legend,
				datasets: [dataset]
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
		} else {
			chartData = undefined;
			chartOptions = undefined;
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
					<RadioItem bind:group={filter}
							   name="justify"
							   value={DataViews[dataView]}
							   on:change={loadEntriesFiltered}>
						{dataView}
					</RadioItem>
				{/each}
			</RadioGroup>

			{#if chartData }
				<Line data={chartData} options={chartOptions} />
			{:else}
				<div class="flex flex-col items-center text-center gap-4">
					<NoScale width={100} height={100}/>
					<p>
						Insufficient data for to render your history.
					</p>
				</div>
			{/if}
		</div>
	</div>
</section>