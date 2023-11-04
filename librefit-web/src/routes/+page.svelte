<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {categoriesAsKeyValue, DataViews, getDaytimeGreeting} from '$lib/util.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import * as ct_crud from '$lib/api/calories-rest.js';
	import * as weight_crud from '$lib/api/weight-rest.js';

	export let data;

	/** @type {Array<CalorieTrackerEntry>} */
	let calorieTrackerEntries = [];

	/** @type {WeightTrackerEntry} */
	let weightTrackerEntry;

	/** @type {Goal} */
	let goal;

	$: calorieTrackerEntries;
	$: weightTrackerEntry
	$: goal;

	$: if (data) {
		calorieTrackerEntries = data.lastCt;
		weightTrackerEntry = data.lastWeight;
		goal = data.lastGoal;
	}

	const toastStore = getToastStore();

	const addCalories = (e) => {
		const id = Math.max(...data.lastCt.map(entry => entry.id).filter(id => id !== undefined)) + 1;

		ct_crud.addEntry(e, loadCalorieTrackerEntries, toastStore, '/', id);
	};

	const updateCalories = (e) => {
		ct_crud.updateEntry(e, loadCalorieTrackerEntries, toastStore, '/');
	};

	const deleteCalories = (e) => {
		ct_crud.deleteEntry(e, loadCalorieTrackerEntries, toastStore, '/', 'type=ct');
	};

	const addWeight = (e) => {
		weight_crud.add(e, loadWeightTracker, toastStore, '/');
	}

	const updateWeight = (e) => {
		weight_crud.update(e, loadWeightTracker, toastStore, '/');
	};

	const setGoal = (e) => {

	};

	const loadCalorieTrackerEntries = async (added) => {
		const response = await fetch(`/?type=ct&added=${added}`, {method: 'GET'});
		const result = response.json();

		calorieTrackerEntries = await result;

		if (response.ok) {
			return result;
		} else {
			throw new Error(result);
		}
	}

	const loadWeightTracker = async (update) => {
		if (update.status === 200 || update.status === 201) {
			const response = await fetch(`/?type=weight&filter=${DataViews.Today}`, {
				method: 'GET'
			});

			const result = response.json();

			weightTrackerEntry = (await result)[0];

			if (response.ok) {
				return result;
			} else {
				throw Error(result);
			}
		} else {
			throw Error(update.status)
		}
	}
</script>

<section>
	{#if data.authenticated === true}
	<div class="container mx-auto p-8 space-y-8">

		<h1>Good {getDaytimeGreeting(new Date())}, {data.user.name}!</h1>
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
				<WeightTracker entries={[]} lastEntry={weightTrackerEntry} {goal}
					on:addWeight={addWeight}
					on:updateWeight={updateWeight}
				   	on:updateGoal={setGoal}
				/>
			</div>
		</div>
	</div>
	{:else}
	<div class="container mx-auto p-8 space-y-8 text-center">
		<h1 class="font-logo">
			<span class="text-primary-500 text-9xl">Libre</span>
			<span class="text-secondary-500 text-9xl">Fit</span>
		</h1>

		<p class="unstyled text-3xl">Welcome to LibreFit, an Open Source calorie tracker!</p>
	</div>
	{/if}
</section>
