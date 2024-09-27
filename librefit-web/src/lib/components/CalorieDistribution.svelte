<script>
    import {goto} from '$app/navigation';
    import Check from '$lib/assets/icons/check.svg?component';
    import History from '$lib/assets/icons/history.svg?component';
    import NoFood from '$lib/assets/icons/food-off.svg?component';
    import Overflow1 from '$lib/assets/icons/overflow-1.svg?component';
    import Overflow2 from '$lib/assets/icons/overflow-2.svg?component';
    import {PolarArea} from 'svelte-chartjs';
    import {Chart, registerables} from 'chart.js';
    import {observeToggle} from '$lib/theme-toggle.js';
    import {createDistributionChart} from '$lib/distribution-chart.js';
    import {getAverageDailyIntake} from '$lib/calorie-util.js';

    Chart.register(...registerables);

    /** @type List<CalorieTracker> */
    export let calorieTracker;

    export let displayClass = '';
    export let displayHeader = true;
    export let displayHistory = true;
    export let headerText = 'Average distribution';

    /** @type Array<FoodCategory> */
    export let foodCategories;

    /** @type CalorieTarget */
    export let calorieTarget;

    let polarAreaChart, dailyAverage;

    /**
     * @param {Array<CalorieTracker>} entries
     */
    const refreshChart = (entries) => {
        polarAreaChart = createDistributionChart(entries, foodCategories, displayHistory);
        dailyAverage = getAverageDailyIntake(entries);
    }

    $: calorieTracker, refreshChart(calorieTracker);

    observeToggle(document.documentElement, () => {
        refreshChart(calorieTracker);
    });
</script>

<div class="{displayClass} gap-4 text-center justify-between items-center relative h-full">
    {#if displayHeader}<h2 class="h3">{headerText}</h2>{/if}

    {#if calorieTracker && calorieTracker.length > 0}
        <div class="flex flex-col md:max-2xl:w-fit h-full justify-between gap-4">
            <PolarArea data={polarAreaChart.chartData} options={polarAreaChart.chartOptions}/>

            <div>
                <div class="w-full grid grid-cols-2 gap-2">
                    <div class="text-right">&empty; daily intake:</div>
                    <div class="flex flex-row text">
                        ~{dailyAverage}kcal

                        {#if calorieTarget}
                            {@const targetAverageRatio = dailyAverage / calorieTarget.targetCalories}
                            <span>
                                {#if targetAverageRatio <= 1}
                                    <Check color="rgb(var(--color-primary-700))"/>
                                {:else if targetAverageRatio > 1 && targetAverageRatio <= 1.15}
                                    <Overflow1 color="rgb(var(--color-warning-500))"/>
                                {:else}
                                    <Overflow2 color="rgb(var(--color-error-500))"/>
                                {/if}
                            </span>
                        {/if}
                    </div>

                    {#if calorieTarget}
                        <div class="text-right">
                            &empty; target intake:
                        </div>
                        <div class="text-left">
                            ~{calorieTarget.targetCalories}kcal
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    {:else}
        <div class="flex flex-col gap-4 m-auto">
            <NoFood height={100} width={100} class="self-center"/>
            <p>
                Nothing tracked yet.
            </p>
        </div>
    {/if}

    {#if displayHistory}
        <button class="btn variant-filled w-full" on:click|preventDefault={() => goto('/tracker/calories')}>
            <span>
                <History/>
            </span>
            <span>
                History
            </span>
        </button>
    {/if}
</div>