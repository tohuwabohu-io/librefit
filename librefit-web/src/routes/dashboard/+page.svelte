<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {
		paintWeightTrackerEntries
	} from '$lib/chart.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import {addCalories, addWeight, deleteCalories, updateCalories} from '$lib/api/tracker.js';
	import {getContext} from 'svelte';
	import {Chart, registerables} from 'chart.js';
	import {Line} from 'svelte-chartjs';
	import CalorieDistribution from '$lib/components/CalorieDistribution.svelte';
	import {validateAmount} from '$lib/validation.js';
	import {showToastError, showToastSuccess, showToastWarning} from '$lib/toast.js';
    import { DataViews } from '$lib/enum.js';
    import {getDaytimeGreeting} from '$lib/date.js';
	import {goto} from '$app/navigation';
	import {getFoodCategoryLongvalue} from '$lib/api/category.js';

	Chart.register(...registerables);

	const lastWeightTrackerEntry = getContext('lastWeight');
	const currentGoal = getContext('currentGoal');
	const foodCategories = getContext('foodCategories');
	const ctList = getContext('ctList');

	export let data;

	$: lastWeightTrackerEntry.set(data.lastWeight);
	$: currentGoal.set(data.currentGoal);
	$: ctList.set(data.listCt);
	$: foodCategories.set(data.foodCategories);

	$: ctListRecent = data.lastCt;
	$: wtList = data.listWeight;

	let wtChart;
	$: if (wtList) {
		wtChart = paintWeightTrackerEntries(wtList, new Date(), DataViews.Month);
	}

	const user = getContext('user');

	const indicator = getContext('indicator');

	const toastStore = getToastStore();
	if (!$user) goto('/');


	const onAddCalories = async (event) => {
		const amountMessage = validateAmount(event.detail.value);

		if (!amountMessage) {
			await addCalories(event).then(async response => {
				event.detail.callback();

				ctListRecent = await response;

				showToastSuccess(
					toastStore,
					`Successfully added ${getFoodCategoryLongvalue($foodCategories, event.detail.category)}.`
				);

			}).catch((e) => {
				showToastError(toastStore, e);
				event.detail.callback(true);
			}).finally(() => {$indicator = $indicator.finish()})

		} else {
			showToastWarning(toastStore, amountMessage);
			event.detail.callback(true);
		}
	};

	const onUpdateCalories = async (event) => {
		const amountMessage = validateAmount(event.detail.value);

		if (!amountMessage) {
			$indicator = $indicator.start(event.detail.target);

			await updateCalories(event).then(async response => {
				event.detail.callback();

				ctListRecent = await response;

				showToastSuccess(
					toastStore,
					`Successfully updated ${getFoodCategoryLongvalue($foodCategories, event.detail.category)}.`
				);
			}).catch((e) => {
				showToastError(toastStore, e);
				event.detail.callback(true);
			}).finally(() => $indicator = $indicator.finish());
		} else {
			showToastWarning(toastStore, amountMessage);
			event.detail.callback(true);
		}
	};

	const onDeleteCalories = async (event) => {
		$indicator = $indicator.start(event.detail.target);

		await deleteCalories(event).then(async response => {
			event.detail.callback();

			ctListRecent = await response;

			showToastSuccess(toastStore, `Deletion successful.`);
		}).catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback(true);
		}).finally(() => {$indicator = $indicator.finish()})
	};

	const onAddWeight = async (event) => {
		$indicator = $indicator.start(event.detail.target);

		await addWeight(event).then(async response => {
			lastWeightTrackerEntry.set(await response.json());
		}).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
	}

	const setGoal = (e) => {

	};
</script>

<svelte:head>
	<title>LibreFit - Dashboard</title>
</svelte:head>

{#if $user}
<section>
	<div class="container mx-auto p-8 space-y-8">
		{#if $user}
			{@const name = $user.name}

			<h1>Good {getDaytimeGreeting(new Date())}{#if name}, {$user.name}!{:else}!{/if}</h1>
			<p>This is your daily summary.</p>

			<div class="flex xl:flex-row flex-col gap-8">
				<div class="flex xl:flex-row flex-col gap-4 grow variant-ghost-surface rounded-xl p-4">
					<CalorieTracker entries={ctListRecent} categories={$foodCategories}
									on:addCalories={onAddCalories}
									on:updateCalories={onUpdateCalories}
									on:deleteCalories={onDeleteCalories}
					/>
				</div>
				<div class="variant-ghost-surface rounded-xl p-4 md:flex md:flex-row">
					<WeightTracker displayClass="md:w-1/2"
							on:addWeight={onAddWeight}
							on:updateGoal={setGoal}
					/>

					<CalorieDistribution bind:ctList={$ctList} displayClass="xl:hidden hidden md:flex md:flex-col md:w-1/2"/>
				</div>
			</div>

			<div class="flex md:flex-row flex-col gap-8">
				<CalorieDistribution bind:ctList={$ctList} displayClass="flex flex-col md:max-xl:hidden variant-ghost-surface rounded-xl xl:w-1/4" />

				<div class="flex flex-row gap-4 grow variant-ghost-surface rounded-xl p-4 object-fill xl:w-3/4">
					{#if wtChart}
						<Line options={wtChart.chartOptions} data={wtChart.chartData}/>
					{/if}
				</div>
			</div>
		{/if}
	</div>
</section>
{/if}