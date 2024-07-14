<script>
    import {paintWeightTrackerEntries} from '$lib/weight-chart.js';
    import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
    import {getToastStore} from '@skeletonlabs/skeleton';
    import {
        addCalories,
        addWeight,
        deleteCalories,
        deleteWeight,
        listCalorieTrackerEntriesRange,
        listWeightRange,
        updateCalories,
        updateWeight
    } from '$lib/api/tracker.js';
    import {createCalorieTarget, createWeightTarget} from '$lib/api/target.js';
    import {getContext} from 'svelte';
    import {Chart, registerables} from 'chart.js';
    import {Line} from 'svelte-chartjs';
    import CalorieDistribution from '$lib/components/CalorieDistribution.svelte';
    import {validateAmount} from '$lib/validation.js';
    import {showToastError, showToastSuccess, showToastWarning} from '$lib/toast.js';
    import {DataViews} from '$lib/enum.js';
    import {getDaytimeGreeting} from '$lib/date.js';
    import {goto} from '$app/navigation';
    import {getFoodCategoryLongvalue} from '$lib/api/category.js';
    import {subMonths, subWeeks} from 'date-fns';
    import ScaleOff from '$lib/assets/icons/scale-outline-off.svg?component';
    import {observeToggle} from '$lib/theme-toggle.js';
    import CalorieQuickview from '$lib/components/CalorieQuickview.svelte';
    import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';

    Chart.register(...registerables);

    /** @type Writable<WeightTrackerEntry> */
    const lastWeightTrackerEntry = getContext('lastWeight');

    /** @type Writable<WeightTarget> */
    const weightTarget = getContext('weightTarget');

    /** @type Writable<CalorieTarget> */
    const calorieTarget = getContext('calorieTarget');

    /** @type Writable<List<FoodCategory>> */
    const foodCategories = getContext('foodCategories');

    export let data;

    /** @type Dashboard */
    const dashboardData = data.dashboardData;

    let caloriesToday = dashboardData.caloriesTodayList;
    let weightListToday = dashboardData.weightTodayList;
    let weightListMonth = dashboardData.weightMonthList;

    $: weightChart = paintWeightTrackerEntries(weightListMonth, today, DataViews.Month);
    $: lastWeightTrackerEntry.set(dashboardData.weightTodayList[0]);
    $: weightTarget.set(dashboardData.weightTarget);
    $: calorieTarget.set(dashboardData.calorieTarget);
    $: foodCategories.set(dashboardData.foodCategories);

    $: dashboardData.caloriesWeekList;

    const user = getContext('user');
    const indicator = getContext('indicator');

    const toastStore = getToastStore();

    if (!$user) goto('/');

    const today = new Date();
    const lastWeek = subWeeks(today, 1);
    const lastMonth = subMonths(today, 1);

    observeToggle(document.documentElement, () => repaintWeightChart());

    const onAddCalories = async (event) => {
        const amountMessage = validateAmount(event.detail.value);

        if (!amountMessage) {
            await addCalories(event).then(async response => {
                event.detail.callback();

                caloriesToday = await response;

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

                caloriesToday = await response;

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

            caloriesToday = await response;

            showToastSuccess(toastStore, `Deletion successful.`);
        }).then(refreshCalorieDistribution).catch((e) => {
            showToastError(toastStore, e);
            event.detail.callback(true);
        }).finally(() => $indicator = $indicator.finish())
    };

    const onAddWeight = async (event) => {
        const amountMessage = validateAmount(event.detail.value);

        if (!amountMessage) {
            $indicator = $indicator.start(event.detail.target);

            await addWeight(event).then(async response => {
                event.detail.callback();

                weightListToday = await response.json();
                lastWeightTrackerEntry.set(weightListToday[0]);

                showToastSuccess(toastStore, `Set weight to ${$lastWeightTrackerEntry.amount}kg.`);
            }).then(refreshWeightChart).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
        } else {
            showToastWarning(toastStore, amountMessage);
            event.detail.callback(true);
        }
    }

    const onUpdateWeight = async (event) => {
        const amountMessage = validateAmount(event.detail.value);

        if (!amountMessage) {
            $indicator = $indicator.start(event.detail.target);

            await updateWeight(event).then(async response => {
				event.detail.callback();

                weightListToday = await response.json();

				showToastSuccess(toastStore, 'Successfully updated weight.');
            }).then(refreshWeightChart).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
        } else {
            showToastWarning(toastStore, amountMessage);
            event.detail.callback(true);
        }
    }

    const onDeleteWeight = async (event) => {
		$indicator = $indicator.start(event.detail.target);

		await deleteWeight(event).then(async (response) => {
			event.detail.callback();

            weightListToday = await response.json();

			showToastSuccess(toastStore, `Deletion successful.`);
		}).then(refreshWeightChart).catch((e) => {
			showToastError(toastStore, e);
			event.detail.callback(true);
		}).finally(() => $indicator = $indicator.finish())
    }

    const refreshCalorieDistribution = async () => {
        const calorieTrackerRangeResponse = await listCalorieTrackerEntriesRange(lastWeek, today);

        if (calorieTrackerRangeResponse.ok) {
            dashboardData.caloriesWeekList = await calorieTrackerRangeResponse.json();
        }
    }

    const refreshWeightChart = async () => {
        const weightRangeResponse = await listWeightRange(lastMonth, today);

        if (weightRangeResponse.ok) {
            weightListMonth = await weightRangeResponse.json();

            repaintWeightChart();
        }
    }

    const repaintWeightChart = () => {
        weightChart = paintWeightTrackerEntries(weightListMonth, today, DataViews.Month);
    }

    const setCalorieTarget = async (e) => {
        $indicator = $indicator.start(e.detail.target);

        await createCalorieTarget(e.detail.calorieTarget).then(async response => {
            calorieTarget.set(response);
        }).then(() => {
            showToastSuccess(toastStore, 'Successfully set target.');
        }).catch((e) => {
            showToastError(toastStore, e);
        }).finally(() => $indicator = $indicator.finish());
    }

    const setWeightTarget = async (e) => {
        $indicator = $indicator.start(e.detail.target);

        await createWeightTarget(e.detail.weightTarget).then(async response => {
            calorieTarget.set(response);
        }).then(() => {
            showToastSuccess(toastStore, 'Successfully set target.');
        }).catch((e) => {
            showToastError(toastStore, e);
        }).finally(() => $indicator = $indicator.finish());
    }
</script>

<svelte:head>
    <title>LibreFit - Dashboard</title>
</svelte:head>

{#if $user}
    <section>
        <div class="container md:w-fit mx-auto p-8 space-y-8">
            {#if $user}
                {@const name = $user.name}

                <h1 class="h1">Good {getDaytimeGreeting(new Date())}{#if name}, {name}!{:else}!{/if}</h1>
                <p>This is your daily summary.</p>

                <div class="flex flex-col gap-8 lg:grid grid-cols-3">
                    <div class="card flex flex-col gap-4 p-4">
                        <CalorieTracker calorieTrackerEntries={caloriesToday}
                                        categories={$foodCategories}
                                        bind:calorieTarget={$calorieTarget}
                                        on:addCalories={onAddCalories}
                                        on:updateCalories={onUpdateCalories}
                                        on:deleteCalories={onDeleteCalories}
                        />
                    </div>

                    <div class="card flex flex-col gap-4 p-4">
                        <CalorieDistribution displayClass="flex flex-col"
                                             bind:calorieTrackerEntries={dashboardData.caloriesWeekList}
                                             foodCategories={$foodCategories}
                                             bind:calorieTarget={dashboardData.calorieTarget}
                        />
                    </div>

                    <div class="card p-4">
                        <CalorieQuickview displayClass="flex flex-col"
                                          bind:calorieTrackerEntries={dashboardData.caloriesWeekList}
                                          calorieTarget={$calorieTarget}
                                          on:setTarget={setCalorieTarget}
                        />
                    </div>
                </div>

                <div class="flex md:flex-row flex-col gap-8">
                    <div class="flex flex-col gap-4 card p-4 object-fill justify-center items-center relative md:w-full">
                        <h2 class="h3">Weight Tracker</h2>
                        {#if weightChart && weightListMonth.length > 0}
                            <Line class="md:w-full" options={weightChart.chartOptions} data={weightChart.chartData}/>
                        {:else}
                            <div>
                                <ScaleOff width={100} height={100} class="self-center"/>
                            </div>
                        {/if}
                        <WeightTracker weightList={weightListToday}
                                       weightTarget={$weightTarget}
                                       on:addWeight={onAddWeight}
                                       on:updateWeight={onUpdateWeight}
                                       on:deleteWeight={onDeleteWeight}
                        />
                    </div>
                </div>
            {/if}
        </div>
    </section>
{/if}