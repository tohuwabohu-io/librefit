<script>
	import TdeeStepper from '$lib/components/TdeeStepper.svelte';
	import {getModalStore, getToastStore} from '@skeletonlabs/skeleton';
	import {showToastSuccess, showToastError} from '$lib/toast.js';
	import ValidatedInput from '$lib/components/ValidatedInput.svelte';
	import {bmiCategoriesAsKeyValue, getDateAsStr} from '$lib/util.js';
	import * as dateUtil from 'date-fns';
	import {getContext} from 'svelte';

	const modalStore = getModalStore();
	const toastStore = getToastStore();

	const lastWeightEntry = getContext('lastWeight');
	const currentGoal = getContext('currentGoal');

	/** @type Tdee */
	let calculationResult;

	let calculationError;

	const bmiCategories = bmiCategoriesAsKeyValue;
	const today = new Date();

	const calculate = async (e) => {
		await fetch('/wizard', {
			method: 'POST',
			body: JSON.stringify({tdee: e.detail.tdee}),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(async (response) => {
			if (response.ok) {
				calculationResult = await response.json();
			} else {
				calculationError = true;
			}
		}).catch(console.error);
	}

	const reset = () => {
		calculationResult = undefined;
		calculationError = false;
	}

	/** @param {Tdee} calculationResult */
	const showGoalModal = (calculationResult) => {
		const endDate = dateUtil.addDays(today, calculationResult.durationDays);

		modalStore.trigger({
			type: 'component',
			component: 'goalModal',
			meta: {
				/** @type Goal */
				goal: {
					added: getDateAsStr(today),
					endDate: getDateAsStr(endDate),
					startDate: getDateAsStr(today),
					initialWeight: calculationResult.weight,
					targetWeight: calculationResult.targetWeight,
					targetCalories: calculationResult.target,
					maximumCalories: calculationResult.tdee
				}
			},
			response: async (e) => {
				if (!e.cancelled) {
					await createGoal(e.goal);
					await createWeightTrackerEntry(e.goal)
				}

				modalStore.close();
			}
		})
	}

	const createGoal = async (goal) => {
		await fetch('/wizard', {
			method: 'POST',
			body: JSON.stringify({
				goal: goal
			}),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(async response => {
			if (response.ok) {
				showToastSuccess(toastStore, 'Successfully created goal.');

				currentGoal.set(await response.json());
			} else {
				throw Error();
			}
		}).catch((error) => showToastError(toastStore, error))
	}

	const createWeightTrackerEntry = async (goal) => {
		await fetch('/wizard', {
			method: 'POST',
			body: JSON.stringify({
				/** @type WeightTrackerEntry */
				weight: {
					added: getDateAsStr(today),
					amount: goal.initialWeight
				}}
			),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(async response => {
			if (response.ok) {
				lastWeightEntry.set(await response.json());
			}

			// fail silently
		}).catch(_ => {})
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

			<div class="table-container">
				<table class="table table-hover">
					<tr>
						<td>
							BMR (Basal Metabolic Rate)
						</td>
						<td>
							<ValidatedInput unit="kcal" value={calculationResult.bmr} readOnly={true}/>
						</td>
					</tr>

					<tr>
						<td>
							TDEE (Total Daily Energy Expediture)
						</td>
						<td>
							<ValidatedInput unit="kcal" value={calculationResult.tdee} readOnly={true}/>
						</td>
					</tr>

					<tr>
						<td>
							Target deficit
						</td>
						<td>
							<ValidatedInput unit="kcal" value={calculationResult.deficit} readOnly={true}/>
						</td>
					</tr>

					<tr>
						<td>
							Target intake
						</td>
						<td>
							<ValidatedInput unit="kcal" value={calculationResult.target} readOnly={true}/>
						</td>
					</tr>
				</table>
			</div>

			<h2 class="h2">Body parameters</h2>

			<div class="table-container">
				<table class="table table-hover">
					<tr>
						<td>
							Age
						</td>
						<td>
							<ValidatedInput value={calculationResult.age} readOnly={true} />
						</td>
					</tr>

					<tr>
						<td>
							Height
						</td>
						<td>
							<ValidatedInput unit="cm" value={calculationResult.height} readOnly={true}/>
						</td>
					</tr>

					<tr>
						<td>
							Weight
						</td>
						<td>
							<ValidatedInput unit="kg" value={calculationResult.weight} readOnly={true}/>
						</td>
					</tr>

					<tr>
						<td>
							Body Mass Index
						</td>
						<td>
							<ValidatedInput value={calculationResult.bmi} readOnly={true}/>
						</td>
					</tr>

					<tr>
						<td>
							Classification
						</td>
						<td>
							<select class="select" disabled bind:value={calculationResult.bmiCategory}>
								{#each bmiCategories as bmiCategory}
									<option value={bmiCategory.value}>{bmiCategory.label}</option>
								{/each}
							</select>
						</td>
					</tr>
				</table>
			</div>


			<h2 class="h2">Next steps</h2>
			<p>
				Based on your input, your basal metabolic rate is {calculationResult.bmr}kcal. Your daily calorie
				consumption to hold your weight should be around {calculationResult.tdee}kcal.
			</p>

			<p>
				Having {calculationResult.weight}kg at {calculationResult.height}cm height means you have a BMI of
				{calculationResult.bmi}.

				{#if calculationResult.targetBmi}
					At your age of {calculationResult.age},

					{@const bmiMin = calculationResult.targetBmi[0]}
					{@const bmiMax = calculationResult.targetBmi[1]}

					{#if bmiMin <= calculationResult.bmi && calculationResult.bmi <= bmiMax}
						you are currently in
					{:else}
						you are out of
					{/if}

					the optimal BMI range of {bmiMin} to {bmiMax}, leaving you
					{bmiCategories.filter(e => e.value === calculationResult.bmiCategory)[0].label}.
					Your weight should be around {calculationResult.targetWeight}kg.
				{/if}
			</p>

			<p>
				To reach the optimal weight within the standard weight range, you will need to consume calories at a
				difference of {calculationResult.deficit}kcal for {calculationResult.durationDays} days.
				Your caloric intake should be around {calculationResult.target}kcal during that time.
			</p>

			<p>
				If you like to proceed, you can create a new goal taking over those values. Alternatively, you can
				define a custom goal on the dashboard.
			</p>

			<div class="flex flex-grow justify-between">
				<button on:click|preventDefault={reset} class="btn variant-filled">Recalculate</button>
				<button on:click|preventDefault={() => showGoalModal(calculationResult)} class="btn variant-filled-primary">Create Goal</button>
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
