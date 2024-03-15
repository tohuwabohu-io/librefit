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
	import {validateAmount} from '$lib/validation.js';
	import {showToastWarning} from '$lib/toast.js';

	Chart.register(...registerables);

	export let data;

	let calorieTrackerEntries;
	$: calorieTrackerEntries;

	$: if (data) {
		calorieTrackerEntries = data.lastCt;
	}

	const user = getContext('user');
	const weightTrackerEntry = getContext('lastWeight');
	const indicator = getContext('indicator');

	const toastStore = getToastStore();

	const addCalories = (e) => {
		const amountMessage = validateAmount(e.detail.value);

		if (!amountMessage) {
			$indicator = $indicator.start(e.detail.target);
			ct_crud.addEntry(e, loadCalorieTrackerEntries, toastStore, '/dashboard');
		} else {
			showToastWarning(toastStore, amountMessage);
			e.detail.callback(true);
		}
	};

	const updateCalories = (e) => {
		const amountMessage = validateAmount(e.detail.value);

		if (!amountMessage) {
			$indicator = $indicator.start(e.detail.target);
			ct_crud.updateEntry(e, loadCalorieTrackerEntries, toastStore, '/dashboard');
		} else {
			showToastWarning(toastStore, amountMessage);
			e.detail.callback(true);
		}
	};

	const deleteCalories = (e) => {
		$indicator = $indicator.start(e.detail.target);

		ct_crud.deleteEntry(e, loadCalorieTrackerEntries, toastStore, '/dashboard', 'type=ct');
	};

	const addWeight = (e) => {
		weight_crud.add(e, updateWeightTracker, toastStore, '/dashboard');
	}

	const setGoal = (e) => {

	};

	const loadCalorieTrackerEntries = async (added, trackerCallback) => {
		$indicator = $indicator.finish();
		setTimeout(() => {
			$indicator = $indicator.hide();
		}, 500);

		trackerCallback(added === undefined);

		if (added) {
			const response = await fetch(`/dashboard?type=ct&added=${added}`, {method: 'GET'});

			const result = response.json();

			calorieTrackerEntries = await result;

			if (!response.ok) {
				throw new Error(await result);
			}
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
			{@const name = $user.name}

			<h1>Good {getDaytimeGreeting(new Date())}{#if name}, {$user.name}!{:else}!{/if}</h1>
			<p>This is your daily summary.</p>

			<div class="flex xl:flex-row flex-col gap-8">
				<div class="flex xl:flex-row flex-col gap-4 grow variant-ghost-surface rounded-xl p-4">
					<CalorieTracker entries={calorieTrackerEntries} categories={categoriesAsKeyValue}
									on:addCalories={addCalories}
									on:updateCalories={updateCalories}
									on:deleteCalories={deleteCalories}
					/>
				</div>
				<div class="variant-ghost-surface rounded-xl p-4 md:flex md:flex-row">
					<WeightTracker displayClass="md:w-1/2"
							on:addWeight={addWeight}
							on:updateGoal={setGoal}
					/>

					<CalorieDistribution bind:data={data} displayClass="xl:hidden hidden md:flex md:flex-col md:w-1/2"/>
				</div>
			</div>

			<div class="flex md:flex-row flex-col gap-8">
				<CalorieDistribution bind:data={data} displayClass="flex flex-col md:max-xl:hidden variant-ghost-surface rounded-xl xl:w-1/4" />

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
