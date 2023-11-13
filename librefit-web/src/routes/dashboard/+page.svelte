<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {categoriesAsKeyValue, getDaytimeGreeting} from '$lib/util.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import * as ct_crud from '$lib/api/calories-rest.js';
	import * as weight_crud from '$lib/api/weight-rest.js';
	import {getContext, setContext} from 'svelte';
	import {writable} from 'svelte/store';

	export let data;

	let calorieTrackerEntries;

	const weightTrackerEntry = writable();
	const goal = writable();

	$: calorieTrackerEntries;
	$: weightTrackerEntry.set(data.lastWeight);
	$: goal.set(data.goal);

	setContext('lastWeight', weightTrackerEntry);
	setContext('goal', goal);

	$: if (data) {
		calorieTrackerEntries = data.lastCt;
	}

	const user = getContext('user');

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
			const response = await fetch(`/dashboard?type=weight`, {
				method: 'GET'
			});

			const result = response.json();

			weightTrackerEntry.set((await result)[0]);

			if (response.ok) {
				return result;
			} else {
				throw Error(await result);
			}
		} else {
			throw Error(update.status)
		}
	}
</script>

<section>
	<div class="container mx-auto p-8 space-y-8">

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
	</div>
</section>
