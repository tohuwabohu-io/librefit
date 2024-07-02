<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {
		paintWeightTrackerEntries
	} from '$lib/chart.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import {getToastStore} from '@skeletonlabs/skeleton';
	import {addCalories, addWeight, deleteCalories, updateCalories, listCalorieTrackerEntriesRange, listWeightRange} from '$lib/api/tracker.js';
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
	import {subMonths} from 'date-fns';
	import ScaleOff from '$lib/assets/icons/scale-outline-off.svg';
	import {observeToggle} from '$lib/theme-toggle.js';

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
	$: wtListRecent = data.listWeight;
	$: wtChart = paintWeightTrackerEntries(wtListRecent, today, DataViews.Month);

	const user = getContext('user');

	const indicator = getContext('indicator');

	const toastStore = getToastStore();
	if (!$user) goto('/');

	const today = new Date();
	const lastMonth = subMonths(today, 1);

	observeToggle(document.documentElement, () => repaintWeightChart());

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

			}).then(refreshCalorieDistribution).catch((e) => {
				showToastError(toastStore, e);
				event.detail.callback(true);
			}).finally(() => $indicator = $indicator.finish())

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
			}).then(refreshCalorieDistribution).catch((e) => {
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
		}).then(refreshCalorieDistribution).catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback(true);
		}).finally(() => $indicator = $indicator.finish())
	};

	const onAddWeight = async (event) => {
		$indicator = $indicator.start(event.detail.target);

		await addWeight(event).then(async response => {
			lastWeightTrackerEntry.set(await response.json());
		}).then(refreshWeightChart).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
	}

	const refreshCalorieDistribution = async () => {
		const calorieTrackerRangeResponse = await listCalorieTrackerEntriesRange(lastMonth, today);

		if (calorieTrackerRangeResponse.ok) {
			ctList.set(await calorieTrackerRangeResponse.json());
		}
	}

	const refreshWeightChart = async () => {
		const weightRangeResponse = await listWeightRange(lastMonth, today);

		if (weightRangeResponse.ok) {
			wtListRecent = await weightRangeResponse.json();

			repaintWeightChart();
		}
	}

	const repaintWeightChart = () => {
		wtChart = paintWeightTrackerEntries(wtListRecent, today, DataViews.Month);
	}

	const setGoal = (e) => {

	};
</script>

<svelte:head>
	<title>LibreFit - Dashboard</title>
</svelte:head>

{#if $user}
<section>
	<div class="container md:w-fit mx-auto p-8 space-y-8">
		{#if $user}
			{@const name = $user.name}

			<h1 class="h1">Good {getDaytimeGreeting(new Date())}{#if name}, {$user.name}!{:else}!{/if}</h1>
			<p>This is your daily summary.</p>

			<div class="flex lg:flex-row flex-col gap-8">
				<div class="card flex xl:flex-row flex-col gap-4 p-4 lg:w-1/3">
					<CalorieTracker entries={ctListRecent} categories={$foodCategories} currentGoal={$currentGoal}
									on:addCalories={onAddCalories}
									on:updateCalories={onUpdateCalories}
									on:deleteCalories={onDeleteCalories}
					/>
				</div>

				<div class="card flex xl:flex-row flex-col gap-4 p-4 lg:w-1/3">
					<CalorieDistribution displayClass="flex flex-col"
						            bind:ctList={$ctList}
					/>
				</div>

				<div class="card md:flex md:flex-row lg:w-1/3 p-4">
					<WeightTracker displayClass="md:w-1/2"
							on:addWeight={onAddWeight}
							on:updateGoal={setGoal}
						    lastEntry={$lastWeightTrackerEntry}
						    currentGoal={$currentGoal}
					/>
				</div>
			</div>

			<div class="flex md:flex-row flex-col gap-8">
				<div class="flex flex-row gap-4 card p-4 object-fill justify-center items-center relative md:w-full">
					{#if wtChart && data.listWeight.length > 0}
						<Line class="md:w-full" options={wtChart.chartOptions} data={wtChart.chartData}/>
					{:else}
						<div>
							<ScaleOff width={100} height={100} class="self-center"/>
						</div>
					{/if}
				</div>
			</div>
		{/if}
	</div>
</section>
{/if}