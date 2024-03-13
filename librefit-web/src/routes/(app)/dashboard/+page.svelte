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
	import {goto} from '$app/navigation';
	import Overflow1 from '$lib/assets/icons/overflow-1.svg?component';
	import Overflow2 from '$lib/assets/icons/overflow-2.svg?component';
	import Check from '$lib/assets/icons/check.svg?component';

	Chart.register(...registerables);

	export let data;

	let calorieTrackerEntries;
	$: calorieTrackerEntries;

	$: if (data) {
		calorieTrackerEntries = data.lastCt;
	}

	const user = getContext('user');
	const weightTrackerEntry = getContext('lastWeight');
	const currentGoal = getContext('currentGoal');

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
		weight_crud.add(e, updateWeightTracker, toastStore, '/dashboard');
	}

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

	const updateWeightTracker = async (response) => {
		// const response = await fetch(`/dashboard?type=weight&operation=last`, {method: 'GET'});
		const result = await response.json();

		/** @type WeightTrackerEntry */
		const updatedEntry = await result;

		weightTrackerEntry.set(updatedEntry);
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

		const averageCategoryIntake = getAverageCategoryIntake(entries)

		if (averageCategoryIntake != null) {
			for (let cat of Object.keys(Category)) {
				const averageIntake = averageCategoryIntake.get(cat);

				if (averageIntake > 0) {
					values.push(averageCategoryIntake.get(cat));
					labels.push(cat);
				}
			}
		}

		return {
			labels: labels,
			datasets: [{
				label: '∅ kcal',
				data: values,
				hoverOffset: 4,
				backgroundColor: [
					'rgb(182 200 0 / .3)',
					'rgb(140 67 210 / .3)',
					'rgb(14 165 233 / .3)',
					'rgb(234 179 8 / .3)',
					'rgb(165 29 45 / .3)'
				]
			}]
		};
	}

	const getAverageCategoryIntake = (entries) => {
		const nonEmpty = entries.filter(e => e.amount > 0);

		if (nonEmpty.length > 0) {
			const catMap = new Map();

			const sum = nonEmpty.map(e => e.amount).reduce((a, b) => a + b);
			const dailyAverage = getAverageDailyIntake(entries);

			for (let cat of Object.keys(Category)) {
				const catEntries = nonEmpty.filter(e => e.category === Category[cat]);

				if (catEntries.length > 0) {
					const catSum = catEntries.map(e => e.amount).reduce((a, b) => a + b);

					catMap.set(cat, Math.round(dailyAverage * (catSum / sum)));
				}

			}

			return catMap;
		}

		return null;
	}

	/** @param {Array<CalorieTrackerEntry>} entries */
	const getAverageDailyIntake = (entries) => {
		const nonEmpty = entries.filter(e => e.amount > 0);

		if (nonEmpty.length > 0) {
			const days = new Set(nonEmpty.map(e => e.added));

			let sum = 0;

			for (let day of days) {
				sum += entries.filter(e => e.added === day).map(e => e.amount).reduce((a, b) => a + b)
			}

			return Math.round(sum / days.size);
		}

		return 0;
	}
</script>

<svelte:head>
	<title>LibreFit - Dashboard</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-8">
		{#if $user}
			<h1>Good {getDaytimeGreeting(new Date())}, {$user.name}!</h1>
			<h2>This is your daily summary.</h2>

			<div class="flex md:flex-row flex-col gap-8">
				<div class="flex md:flex-row flex-col gap-4 grow variant-ghost-surface rounded-xl p-4">
					<CalorieTracker entries={calorieTrackerEntries} categories={categoriesAsKeyValue}
									on:addCalories={addCalories}
									on:updateCalories={updateCalories}
									on:deleteCalories={deleteCalories}
					/>
				</div>
				<div class="variant-ghost-surface rounded-xl p-4">
					<WeightTracker
							on:addWeight={addWeight}
							on:updateGoal={setGoal}
					/>
				</div>
			</div>

			<div class="flex md:flex-row flex-col gap-8">
				<div class="variant-ghost-surface rounded-xl p-4 flex flex-col text-center justify-between">
					{#await data.listCt}
						<p>Loading...</p>
					{:then ctList}
						{@const data = getData(ctList)}
						{@const options = getConfig(data)}
						{@const dailyAverage = getAverageDailyIntake(ctList)}

						<h3 class="h3">Average distribution</h3>

						<PolarArea {options} {data}/>

						<div>
							<div class="w-full grid grid-cols-[auto_1fr_auto]">
								<div>&empty; daily intake:</div>
								<div>
									~{dailyAverage}kcal
								</div>
								<div>
									{#if $currentGoal}
										{@const targetAverageRatio = dailyAverage / $currentGoal.targetCalories}
										<span>
										{#if targetAverageRatio <= 1}
											<Check color="rgb(var(--color-primary-700))"/>
										{:else if targetAverageRatio > 1 && targetAverageRatio <= 1.15}
											<Overflow1 color="rgb(var(--color-warning-500))"/>
										{:else}
											<Overflow2 color="rgb(var(--color-error-500))"/>
										{/if}
									</span>
									{/if}
								</div>

								{#if $currentGoal}
									<div>
										&empty; target intake:
									</div>
									<div>
										~{$currentGoal.targetCalories}kcal
									</div>
									<div>

									</div>
								{/if}
							</div>
						</div>

						<button class="btn variant-filled" on:click|preventDefault={() => goto('/tracker/calories')}>Show history</button>
					{/await}
				</div>

				<div class="flex flex-row gap-4 grow variant-ghost-surface rounded-xl p-4">
					{#await data.listWeight}
						<p>Loading...</p>
					{:then weightList}
						{@const meta = paintWeightTrackerEntries(weightList, new Date(), DataViews.Month)}

						<Line options={meta.chartOptions} data={meta.chartData}/>
					{/await}
				</div>
			</div>
		{/if}
	</div>
</section>
