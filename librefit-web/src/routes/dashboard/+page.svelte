<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {categoriesAsKeyValue, DataViews, getDaytimeGreeting, paintWeightTrackerEntries} from '$lib/util.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import * as ct_crud from '$lib/api/calories-rest.js';
	import * as weight_crud from '$lib/api/weight-rest.js';
	import {getContext} from 'svelte';
	import {Chart, registerables} from 'chart.js';
	import {Line, PolarArea} from 'svelte-chartjs';
	import {Category} from '$lib/api/model.js';

	Chart.register(...registerables);

	export let data;

	let calorieTrackerEntries;
	$: calorieTrackerEntries;

	$: if (data) {
		calorieTrackerEntries = data.lastCt;
	}

	const user = getContext('user');
	const weightTrackerEntry = getContext('lastWeight');

	const toastStore = getToastStore();

	const addCalories = (e) => {
		ct_crud.addEntry(e, loadCalorieTrackerEntries, toastStore, '/dashboard');
	};

	const updateCalories = (e) => {
		ct_crud.updateEntry(e, loadCalorieTrackerEntries, toastStore, '/dashboard');
	};

	const deleteCalories = (e) => {
		ct_crud.deleteEntry(e, loadCalorieTrackerEntries, toastStore, '/dashboard', 'type=ct');
	};

	const addWeight = (e) => {
		weight_crud.add(e, loadWeightTracker, toastStore, '/dashboard');
	}

	const updateWeight = (e) => {
		weight_crud.update(e, loadWeightTracker, toastStore, '/dashboard');
	};

	const setGoal = (e) => {

	};

	const loadCalorieTrackerEntries = async (added) => {
		const response = await fetch(`/dashboard?type=ct&added=${added}`, {method: 'GET'});
		const result = response.json();

		calorieTrackerEntries = await result;

		if (response.ok) {
			return result;
		} else {
			throw new Error(await result);
		}
	}

	const loadWeightTracker = async (update) => {
		if (update.status === 200 || update.status === 201) {
			const response = await fetch(`/dashboard?type=weight&operation=last`, {
				method: 'GET'
			});

			const result = await response.json();
			weightTrackerEntry.set(result);

			if (response.ok) {
				return result;
			} else {
				throw Error(await result);
			}
		} else {
			console.log(update);
			throw Error(update.status)
		}
	}

	const getConfig = (chartData) => {
		return {
			type: 'polarArea',
			data: chartData,
			options: {
				responsive: true,
				plugins: {
					legend: {
						position: 'top',
					},
					title: {
						display: true,
						text: 'Chart.js Polar Area Chart'
					}
				}
			},
		};
	}

	/**
	 * @param {Array<CalorieTrackerEntry>} entries
	 */
	const getData = (entries) => {
		const labels = [];
		const values = [];

		for (let cat of Object.keys(Category)) {
			const amounts = entries.filter(e => e.category === Category[cat] && e.amount > 0).map(e => e.amount);

			if (amounts.length > 0) {
				values.push(amounts.reduce((a, b) => a + b));
				labels.push(cat);
			}
		}

		return {
			labels: labels,
			datasets: [{
				label: 'Total kcal',
				data: values,
				hoverOffset: 4
			}]
		};
	}
</script>

<section>
	<div class="container mx-auto p-8 space-y-8">
		{#if $user}
			<h1>Good {getDaytimeGreeting(new Date())}, {$user.name}!</h1>
			<h2>This is your daily summary.</h2>

			<div class="flex flex-row gap-8">
				<div class="flex flex-row gap-4 grow variant-ghost-surface rounded-xl p-4">
					<CalorieTracker entries={calorieTrackerEntries} categories={categoriesAsKeyValue}
									on:addCalories={addCalories}
									on:updateCalories={updateCalories}
									on:deleteCalories={deleteCalories}
					/>
				</div>
				<div class="variant-ghost-surface rounded-xl p-4">
					<WeightTracker
							on:addWeight={addWeight}
							on:updateWeight={updateWeight}
							on:updateGoal={setGoal}
					/>
				</div>
			</div>

			<div class="flex flex-row gap-8">
				<div class="variant-ghost-surface rounded-xl p-4">
					{#await data.listCt}
						<p>Loading...</p>
					{:then ctList}
						{@const data = getData(ctList)}
						{@const options = getConfig(data)}

						<h3 class="h3">Average distribution</h3>

						<PolarArea {options} {data}/>

						<button class="btn variant-filled">Show history</button>
					{:catch error}
						<p>Error.</p>
					{/await}
				</div>

				<div class="flex flex-row gap-4 grow variant-ghost-surface rounded-xl p-4">
					{#await data.listWeight}
						<p>Loading...</p>
					{:then weightList}
						{@const meta = paintWeightTrackerEntries(weightList, new Date(), DataViews.Month)}

						<Line options={meta.chartOptions} data={meta.chartData}/>
					{:catch error}
						<p>Error.</p>
					{/await}
				</div>
			</div>
		{/if}
	</div>
</section>
