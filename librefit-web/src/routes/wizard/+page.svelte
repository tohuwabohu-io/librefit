<script>
	import TdeeStepper from '$lib/components/TdeeStepper.svelte';
	import {getModalStore, getToastStore} from '@skeletonlabs/skeleton';
	import {showToastSuccess, showToastError} from '$lib/toast.js';

	const modalStore = getModalStore();
	const toastStore = getToastStore();

	/** @type Tdee */
	let calculationResult = {}

	let calculationError;

	const calculate = async (e) => {
		await fetch('/wizard', {
			method: 'POST',
			body: JSON.stringify({tdee: e.detail.tdee}),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(async (response) => {
			if (response.ok) {
				calculationResult = await response.json()
				console.log(calculationResult);
			} else {
				calculationError = true;
			}
		}).catch(console.error);
	}

	const reset = () => {
		calculationResult = undefined;
		calculationError = false;
	}

	const showGoalModal = () => {
		modalStore.trigger({
			type: 'component',
			component: 'goalModal',
			response: async (e) => {
				if (!e.cancelled) {
					await createGoal(e.goal);
				}

				modalStore.close();
			}
		})
	}

	const createGoal = async (goal) => {
		await fetch('/wizard', {
			method: 'POST',
			body: JSON.stringify({goal: goal}),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(async response => {
			if (response.ok) {
				showToastSuccess(toastStore, 'Successfully created goal.');
			} else {
				throw Error()
			}
		}).catch((error) => showToastError(toastStore, error))
	}
</script>

<svelte:head>
	<title>LibreFit - TDEE Wizard</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-8">
		<h1 class="h1">TDEE Calculator</h1>

		{#if !calculationResult && !calculationError}
			<TdeeStepper on:calculate={calculate}/>
		{:else if !calculationError}
			<h2 class="h2">Your result</h2>

			<p>
				Based on your input, your basal metabolic rate is {calculationResult.bmr}kcal. Your daily calorie
				consumption to hold your weight should be around {calculationResult.tdee}kcal.
			</p>
			<p>
				Deficit: {calculationResult.deficit}kcal
			</p>
			<p>
				Target: {calculationResult.target}kcal
			</p>

			<div class="flex flex-grow justify-between">
				<button on:click|preventDefault={reset} class="btn variant-filled">Recalculate</button>
				<button on:click|preventDefault={showGoalModal} class="btn variant-filled-primary">Create Goal</button>
			</div>
		{:else}
			<p>
				An error occured. Please try again later.

				<button on:click|preventDefault={reset} class="btn variant-filled">Recalculate</button>
			</p>
		{/if}
	</div>
</section>

<style>
</style>
