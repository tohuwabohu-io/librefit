<script>
	import {RadioGroup, RadioItem, toastStore} from '@skeletonlabs/skeleton';
	import {createWeightChart, createWeightChartDataset, DataViews, enumKeys, weakEntityEquals} from '$lib/util';
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {Line} from 'svelte-chartjs';
	import {Chart, registerables} from 'chart.js';
	import {handleApiError, showToastSuccess} from "$lib/toast.js";

	Chart.register(...registerables);

	export let filter = DataViews.Month;

	/** @type import('./$types').PageData */
	export let data;

	const today = new Date();

	let lastEntry, entries;
	let chartData, chartOptions;
	let currentGoal;

	const loadEntriesFiltered = async () => {
		await fetch(`/weight?filter=${filter}`, {
			method: 'GET'
		}).then(async (result) => {
			paint(await result.json());
		});
	}

	const paint = (entries) => {
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

	$: if (data && data.entries) {
		entries = data.entries;

		paint(data.entries);
	}

	const add = (e) => {
		fetch('/weight', {
			method: 'POST',
			body: JSON.stringify({
				weight: {
					id: e.detail.sequence,
					added: e.detail.todayDateStr,
					amount: e.detail.value
				}
			}),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(reload).catch(handleApiError);
	}

	const update = (e) => {
		fetch('/weight', {
			method: 'PUT',
			body: JSON.stringify({
				weight: {
					id: e.detail.sequence,
					date: e.detail.date,
					amount: e.detail.value
				}
			})
		}).then(reload).catch(handleApiError);
	}

	const remove = (e) => {
		fetch(`/weight?sequence=${e.detail.sequence}&date=${e.detail.date}`, {
			method: 'DELETE'
		}).then(reload).catch(handleApiError)
	}

	const reload = (result) => {
		if (result.status === 200 || result.status === 201) {
			fetch(`/weight?filter=${filter}`, {
				method: 'GET'
			}).then(async (response) => {
				paint(await response.json());

				showToastSuccess('Update successful.');
			}).catch(handleApiError)
		} else {
			throw Error(result.status)
		}
	}

	const updateGoal = (e) => {
		console.log(e);

		let goal = e.detail.goal;

		if (!currentGoal) {
			fetch('/weight', {
				method: 'POST',
				body: JSON.stringify({
					goal: goal
				}),
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(async (response) => {
				currentGoal = response.json();
			}).catch(handleApiError);
		} else {
			fetch('/weight', {
				method: 'PUT',
				body: JSON.stringify({
					goal: goal
				})
			}).then(async (response) => currentGoal = await response.json())
			.catch(handleApiError)
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

			{#if entries}
				<WeightTracker bind:entries={entries}
							   bind:lastEntry={lastEntry}
							   bind:goal={data.goal}
							   on:addWeight={add}
							   on:updateWeight={update}
							   on:deleteWeight={remove}
							   on:updateGoal={updateGoal}
				/>
			{:else}
				<p>Loading...</p>
			{/if}
		</div>
	</div>
</section>