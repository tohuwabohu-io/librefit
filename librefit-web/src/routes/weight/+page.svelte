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

	let entries = [];
	let lastEntry;
	let chartData, chartOptions;
	let currentGoal;

	const loadEntriesFiltered = async () => {
		await fetch(`/weight?filter=${filter}`, {
			method: 'GET'
		}).then(async (result) => {
			entries = await result.json();
			paint()
		});
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
		}).then(async (response) => {
			entries.push(await response.json());
			entries = entries;

			showToastSuccess('Update successful.');
			paint();
		}).catch(handleApiError);
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
		}).then(_ => {
			showToastSuccess('Update successful.');

			paint();
		}).catch(handleApiError);
	}

	const remove = (e) => {
		fetch(`/weight?sequence=${e.detail.sequence}&date=${e.detail.date}`, {
			method: 'DELETE'
		}).then(result => {
			if (result.status === 200) {
				fetch(`/weight?filter=${filter}`, {
					method: 'GET'
				}).then(async (response) => {
					entries = await response.json();
					paint();

					showToastSuccess('Deletion successful.');
				}).catch(handleApiError)

			} else {
				throw Error(result.status)
			}
		}).catch(handleApiError)
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

			{#if data.entries}
				<WeightTracker bind:entries={data.entries}
							   bind:lastEntry={data.lastEntry}
							   bind:goal={data.goal}
							   on:addWeight={add}
							   on:updateWeight={update}
							   on:deleteWeight={remove}
							   on:updateGoal={updateGoal}
				/>
			{/if}
		</div>
	</div>
</section>