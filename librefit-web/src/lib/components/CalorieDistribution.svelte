<script>
    import {goto} from '$app/navigation';
    import Overflow1 from '$lib/assets/icons/overflow-1.svg?component';
    import Overflow2 from '$lib/assets/icons/overflow-2.svg?component';
    import Check from '$lib/assets/icons/check.svg?component';
    import {PolarArea} from 'svelte-chartjs';
    import {Chart, registerables} from 'chart.js';
    import {getContext} from 'svelte';

    Chart.register(...registerables);

    export let ctList;
    export let displayClass = '';
    export let displayHeader = true;

    let chartData, chartOptions, dailyAverage;

    const currentGoal = getContext('currentGoal');

    /** @type Array<FoodCategory> */
    const foodCategories = getContext('foodCategories');

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const refreshChart = (entries) => {
        chartData = getData(entries);
        chartOptions = getConfig(chartData);
        dailyAverage = getAverageDailyIntake(entries);
    }

    $: ctList, refreshChart(ctList);

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const getData = (entries) => {
        const labels = [];
        const values = [];

        const averageCategoryIntake = getAverageCategoryIntake(entries);


        if (averageCategoryIntake != null) {
            $foodCategories.forEach(cat => {
                const averageIntake = averageCategoryIntake.get(cat.shortvalue);

                if (averageIntake > 0) {
                    values.push(averageCategoryIntake.get(cat.shortvalue));
                    labels.push(cat.longvalue);
                }
            });
        }

        return {
            labels: labels,
            datasets: [{
                label: '∅ kcal',
                data: values,
                hoverOffset: 4,
                backgroundColor: [
                    'rgb(182 200 0 / .3)',
                    'rgb(140 67 210 / .3)',
                    'rgb(14 165 233 / .3)',
                    'rgb(234 179 8 / .3)',
                    'rgb(165 29 45 / .3)'
                ]
            }]
        };
    }

    const getAverageCategoryIntake = (entries) => {
        const nonEmpty = entries.filter(e => e.amount > 0);

        if (nonEmpty.length > 0) {
            const catMap = new Map();

            const sum = nonEmpty.map(e => e.amount).reduce((a, b) => a + b);
            const dailyAverage = getAverageDailyIntake(entries);

            $foodCategories.forEach(cat => {
                const catEntries = nonEmpty.filter(e => e.category === cat.shortvalue);

                if (catEntries.length > 0) {
                    const catSum = catEntries.map(e => e.amount).reduce((a, b) => a + b);

                    catMap.set(cat.shortvalue, Math.round(dailyAverage * (catSum / sum)));
                }
            });

            return catMap;
        }

        return null;
    }

    /** @param {Array<CalorieTrackerEntry>} entries */
    const getAverageDailyIntake = (entries) => {
        const nonEmpty = entries.filter(e => e.amount > 0);

        if (nonEmpty.length > 0) {
            const days = new Set(nonEmpty.map(e => e.added));

            let sum = 0;

            for (let day of days) {
                sum += entries.filter(e => e.added === day).map(e => e.amount).reduce((a, b) => a + b)
            }

            return Math.round(sum / days.size);
        }

        return 0;
    }


    const getConfig = (chartData) => {
        return {
            type: 'polarArea',
            data: chartData,
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Chart.js Polar Area Chart'
                    }
                }
            },
            animation: {
                duration: 0
            }
        };
    }

</script>

<div class="{displayClass} p-4 text-center justify-between ">
    {#if ctList}
        {#if displayHeader}<h3 class="h3">Average distribution</h3>{/if}

        <PolarArea options={chartOptions} data={chartData}/>

        <div>
            <div class="w-full grid grid-cols-[auto_1fr_auto]">
                <div>&empty; daily intake:</div>
                <div>
                    ~{dailyAverage}kcal
                </div>
                <div>
                    {#if $currentGoal}
                        {@const targetAverageRatio = dailyAverage / $currentGoal.targetCalories}
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

                {#if $currentGoal}
                    <div>
                        &empty; target intake:
                    </div>
                    <div>
                        ~{$currentGoal.targetCalories}kcal
                    </div>
                    <div>

                    </div>
                {/if}
            </div>
        </div>

        <button class="btn variant-filled" on:click|preventDefault={() => goto('/tracker/calories')}>Show history</button>
    {/if}
</div>