<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {categoriesAsKeyValue, getDaytimeGreeting} from '$lib/util.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {handleApiError, showToastSuccess} from '$lib/toast.js';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import * as ct_crud from '$lib/api/calories-rest.js';

	export let data;

	/** @type {Array<CalorieTrackerEntry>} */
	let calorieTrackerEntries = [];

	$: calorieTrackerEntries;
	$: if (data && data.lastCt) {
		calorieTrackerEntries = data.lastCt;
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

	const setWeight = (e) => {

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
</script>

<section>
	{#if data.authenticated === true}
	<div class="container mx-auto p-8 space-y-8">

		<h1>Good {getDaytimeGreeting(new Date())}, {data.user.name}!</h1>
		<h2>This is your daily summary.</h2>

		<div class="flex flex-row gap-8">
			<div class="flex flex-row gap-4 variant-ghost-surface rounded-xl p-4">
				<CalorieTracker entries={calorieTrackerEntries} categories={categoriesAsKeyValue}
					on:addCalories={addCalories}
					on:updateCalories={updateCalories}
					on:deleteCalories={deleteCalories}
				/>
			</div>
			<div class="variant-ghost-surface rounded-xl p-4">
				<WeightTracker entries={[]} lastEntry={data.lastWeight} goal={data.lastGoal}
					on:updateWeight={setWeight}
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
