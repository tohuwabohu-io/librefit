<script>
    import {paintWeightTracker} from '$lib/weight-chart.js';
    import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
    import {getToastStore} from '@skeletonlabs/skeleton';
    import {
        addCalories,
        addWeight,
        deleteCalories,
        deleteWeight,
        listCalorieTrackerRange,
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
    import {getFoodCategoryLongvalue} from '$lib/api/category.js';
    import {subMonths, subWeeks} from 'date-fns';
    import ScaleOff from '$lib/assets/icons/scale-outline-off.svg?component';
    import {observeToggle} from '$lib/theme-toggle.js';
    import CalorieQuickview from '$lib/components/CalorieQuickview.svelte';
    import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';

    Chart.register(...registerables);

    /** @type Writable<WeightTracker> */
    const lastWeightTracker = getContext('lastWeight');

    /** @type Writable<List<FoodCategory>> */
    const foodCategories = getContext('foodCategories');

    /** @type Writable<CalorieTarget> */
    const calorieTarget = getContext('calorieTarget');

    export let data;

    /** @type Dashboard */
    const dashboardData = data.dashboardData;

    Object.keys(dashboardData).forEach(key => { console.log(key) });

    /** @type List<CalorieTracker> */
    let caloriesToday = dashboardData.caloriesTodayList;

    /** @type List<WeightTracker> */
    let weightListToday = dashboardData.weightTodayList;

    /** @type List<WeightTracker> */
    let weightListMonth = dashboardData.weightMonthList;

    /** @type WeightTracker */
    let weightTarget = dashboardData.weightTarget;

    $: weightChart = paintWeightTracker(weightListMonth, today, DataViews.Month);
    $: lastWeightTracker.set(dashboardData.weightTodayList[0]);
    $: foodCategories.set(dashboardData.foodCategories);
    $: calorieTarget.set(dashboardData.calorieTarget);

    $: dashboardData.caloriesWeekList;

    const user = getContext('user');
    const indicator = getContext('indicator');

    const toastStore = getToastStore();

    if (!$user) user.set(dashboardData.userData);

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
                lastWeightTracker.set(weightListToday[0]);

                showToastSuccess(toastStore, `Set weight to ${$lastWeightTracker.amount}kg.`);
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
        const calorieTrackerRangeResponse = await listCalorieTrackerRange(lastWeek, today);

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
        weightChart = paintWeightTracker(weightListMonth, today, DataViews.Month);
    }

    const setCalorieTarget = async (e) => {
        $indicator = $indicator.start(e.detail.target);

        await createCalorieTarget(e.detail.calorieTarget).then(response => {
            calorieTarget.set(response);
        }).then(() => {
            showToastSuccess(toastStore, 'Successfully set target caloric intake.');
        }).catch((e) => {
            showToastError(toastStore, e);
        }).finally(() => $indicator = $indicator.finish());
    }

    const setWeightTarget = async (e) => {
        $indicator = $indicator.start(e.detail.target);

        await createWeightTarget(e.detail.weightTarget).then(async response => {
            weightTarget = response;
        }).then(() => {
            showToastSuccess(toastStore, 'Successfully set target weight.');
        }).catch((e) => {
            showToastError(toastStore, e);
        }).finally(() => $indicator = $indicator.finish());
    }
</script>

<svelte:head>
    <title>LibreFit - Dashboard</title>
</svelte:head>

<section>
    <div class="container md:w-fit mx-auto p-8 space-y-8">
        {#if $user}
        {@const name = $user.name}
        <h1 class="h1">Good {getDaytimeGreeting(new Date())}{#if name}, {name}!{:else}!{/if}</h1>
        <p>This is your daily summary.</p>
        {/if}
        <div class="flex flex-col gap-8 lg:grid grid-cols-3">
            <div class="card flex flex-col gap-4 p-4">
                <CalorieTracker calorieTracker={caloriesToday}
                                categories={$foodCategories}
                                calorieTarget={$calorieTarget}
                                on:addCalories={onAddCalories}
                                on:updateCalories={onUpdateCalories}
                                on:deleteCalories={onDeleteCalories}
                />
            </div>

            <div class="card flex flex-col gap-4 p-4">
                <CalorieDistribution displayClass="flex flex-col"
                                     foodCategories={$foodCategories}
                                     bind:calorieTracker={dashboardData.caloriesWeekList}
                                     bind:calorieTarget={dashboardData.calorieTarget}
                />
            </div>

            <div class="card p-4">
                <CalorieQuickview displayClass="flex flex-col"
                                  bind:calorieTracker={dashboardData.caloriesWeekList}
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
                               weightTarget={weightTarget}
                               on:addWeight={onAddWeight}
                               on:updateWeight={onUpdateWeight}
                               on:deleteWeight={onDeleteWeight}
                               on:setTarget={setWeightTarget}
                />
            </div>
        </div>
    </div>
</section>