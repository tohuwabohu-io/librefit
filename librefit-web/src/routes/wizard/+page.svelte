<script>
	import TdeeStepper from '$lib/components/TdeeStepper.svelte';
	import {getModalStore, getToastStore} from '@skeletonlabs/skeleton';
	import {showToastError, showToastSuccess} from '$lib/toast.js';
	import {addDays} from 'date-fns';
	import {getContext} from 'svelte';
	import {proxyFetch} from '$lib/api/util.js';
	import {api} from '$lib/api/index.js';
	import {bmiCategoriesAsKeyValue} from '$lib/enum.js';
	import {getDateAsStr} from '$lib/date.js';
	import {goto} from '$app/navigation';
	import {postWizardResult} from '$lib/api/wizard.js';

	const modalStore = getModalStore();
	const toastStore = getToastStore();

	const indicator = getContext('indicator');
	const user = getContext('user');

	if (!$user) goto('/');

	/** @type Tdee */
	let calculationResult;

	let calculationError;

	const bmiCategories = bmiCategoriesAsKeyValue;
	const today = new Date();

	const calculate = async (e) => {
		$indicator = $indicator.start();

		await proxyFetch(fetch, api.calculateTdee, e.detail.tdee).then(async response => {
			if (response.ok) {
				calculationResult = await response.json();
			} else {
				calculationError = true;
			}
		}).catch(console.error).finally(() => $indicator = $indicator.finish());
	}

	const reset = () => {
		calculationResult = undefined;
		calculationError = false;
	}

	/** @param {Tdee} calculationResult */
	const showGoalModal = (calculationResult) => {
		const endDate = addDays(today, calculationResult.durationDays);

		modalStore.trigger({
			type: 'component',
			component: 'targetModal',
			meta: {
				/** @type CalorieTarget */
				calorieTarget: {
					added: getDateAsStr(today),
					endDate: getDateAsStr(endDate),
					startDate: getDateAsStr(today),
					targetCalories: calculationResult.target,
					maximumCalories: calculationResult.tdee
				},
                /** @type WeightTarget */
                weightTarget: {
                    added: getDateAsStr(today),
                    endDate: getDateAsStr(endDate),
                    startDate: getDateAsStr(today),
                    initialWeight: calculationResult.weight,
                    targetWeight: calculationResult.targetWeight
                }
			},
			response: async (e) => {
				if (e && !e.cancelled) {
					await createTargetsAddWeight(e);
				}

				modalStore.close();
			}
		})
	}

	const createTargetsAddWeight = async (detail) => {
		$indicator = $indicator.start();

		/** @type {Wizard} */
		const wizard = {
			calorieTarget: detail.calorieTarget,
			weightTarget: detail.weightTarget,
			weightTrackerEntry: {
				added: getDateAsStr(today),
				sequence: 1,
				amount: detail.weightTarget.initialWeight
			}
		}

        await postWizardResult(wizard).then(async _ => showToastSuccess(toastStore, 'Successfully saved your targets.'))
            .catch((error) => showToastError(toastStore, error))
            .finally(() => $indicator = $indicator.finish());
	}
</script>

<svelte:head>
	<title>LibreFit - TDEE Wizard</title>
</svelte:head>

{#if $user}
<section>
	<div class="container mx-auto p-8 space-y-8">
		<h1 class="h1">TDEE Calculator</h1>

		{#if !calculationResult && !calculationError}
			<TdeeStepper on:calculate={calculate}/>
		{:else if !calculationError}
			<h2 class="h2">Your result</h2>

			<div class="table-container">
				<table class="table table-compact">
					<thead>
						<tr>
							<th>Description</th>
							<th colspan="2">Value</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>Basal Metabolic Rate</td>
							<td>kcal</td>
							<td>{calculationResult.bmr}</td>
						</tr>

						<tr>
							<td>Total Daily Energy Expediture</td>
							<td>kcal</td>
							<td>{calculationResult.tdee}</td>
						</tr>

						<tr>
							<td>Target deficit</td>
							<td>kcal</td>
							<td>{calculationResult.deficit}</td>
						</tr>

						<tr>
							<td>Target intake</td>
							<td>kcal</td>
							<td>{calculationResult.target}</td>
						</tr>
					</tbody>
				</table>
			</div>

			<h2 class="h2">Body parameters</h2>

			<div class="table-container">
				<table class="table table-compact">
					<thead>
						<tr>
							<th>Description</th>
							<th colspan="2">Value</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>Age</td>
							<td></td>
							<td>{calculationResult.age}</td>
						</tr>

						<tr>
							<td>Height</td>
							<td>cm</td>
							<td>{calculationResult.height}</td>
						</tr>

						<tr>
							<td>Weight</td>
							<td>kg</td>
							<td>{calculationResult.weight}</td>
						</tr>

						<tr>
							<td>Body Mass Index</td>
							<td></td>
							<td>{calculationResult.bmi}</td>
						</tr>

						<tr>
							<td>Classification</td>
							<td></td>
							<td>{bmiCategories.filter(e => e.value === calculationResult.bmiCategory)[0].label}</td>
						</tr>
					</tbody>
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
{/if}