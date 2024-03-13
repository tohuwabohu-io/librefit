<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {categoriesAsKeyValue, DataViews, getDaytimeGreeting, paintWeightTrackerEntries} from '$lib/util.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import * as ct_crud from '$lib/api/calories-rest.js';
	import * as weight_crud from '$lib/api/weight-rest.js';
	import {getContext} from 'svelte';
	import {Chart, registerables} from 'chart.js';
	import {Line} from 'svelte-chartjs';
	import CalorieDistribution from '$lib/components/CalorieDistribution.svelte';

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
</script>

<svelte:head>
	<title>LibreFit - Dashboard</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-8">
		{#if $user}
			<h1>Good {getDaytimeGreeting(new Date())}, {$user.name}!</h1>
			<h2>This is your daily summary.</h2>

			<div class="flex lg:flex-row flex-col gap-8">
				<div class="flex lg:flex-row flex-col gap-4 grow variant-ghost-surface rounded-xl p-4">
					<CalorieTracker entries={calorieTrackerEntries} categories={categoriesAsKeyValue}
									on:addCalories={addCalories}
									on:updateCalories={updateCalories}
									on:deleteCalories={deleteCalories}
					/>
				</div>
				<div class="variant-ghost-surface rounded-xl p-4 md:flex md:flex-row">
					<WeightTracker
							on:addWeight={addWeight}
							on:updateGoal={setGoal}
					/>

					<CalorieDistribution bind:data={data} displayClass="flex xl:hidden max-sm:hidden"/>
				</div>
			</div>

			<div class="flex md:flex-row flex-col gap-8">
				<CalorieDistribution bind:data={data} displayClass="flex flex-col md:max-lg:hidden variant-ghost-surface rounded-xl xl:w-1/4" />

				<div class="flex flex-row gap-4 grow variant-ghost-surface rounded-xl p-4 object-fill xl:w-3/4">
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
