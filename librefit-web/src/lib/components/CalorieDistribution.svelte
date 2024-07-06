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

    export let ctList;
    export let displayClass = '';
    export let displayHeader = true;
    export let displayHistory = true;
    export let headerText = 'Average distribution';

    /** @type Array<FoodCategory> */
    export let foodCategories;

    /** @type Goal */
    export let currentGoal;

    let polarAreaChart, dailyAverage;

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const refreshChart = (entries) => {
        polarAreaChart = createDistributionChart(entries, foodCategories, displayHistory);
        dailyAverage = getAverageDailyIntake(entries);
    }

    $: ctList, refreshChart(ctList);

    observeToggle(document.documentElement, () => {
        refreshChart(ctList);
    });
</script>

<div class="{displayClass} gap-4 text-center justify-between items-center relative h-full">
    {#if ctList && ctList.length > 0}
        {#if displayHeader}<h2 class="h3">{headerText}</h2>{/if}

        <div class="flex flex-col w-fit h-full justify-between gap-4">
            <PolarArea data={polarAreaChart.chartData} options={polarAreaChart.chartOptions}/>

            <div>
                <div class="w-full grid grid-cols-2 gap-2">
                    <div class="text-right">&empty; daily intake:</div>
                    <div class="flex flex-row text">
                        ~{dailyAverage}kcal

                        {#if currentGoal}
                            {@const targetAverageRatio = dailyAverage / currentGoal.targetCalories}
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

                    {#if currentGoal}
                        <div class="text-right">
                            &empty; target intake:
                        </div>
                        <div class="text-left">
                            ~{currentGoal.targetCalories}kcal
                        </div>
                    {/if}
                </div>
            </div>
        </div>

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
    {:else}
        <div>
            <NoFood height={100} width={100}/>
        </div>
    {/if}
</div>