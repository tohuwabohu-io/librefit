<script>
    import {goto} from '$app/navigation';
    import Check from '$lib/assets/icons/check.svg?component';
    import History from '$lib/assets/icons/history.svg?component';
    import NoFood from '$lib/assets/icons/food-off.svg?component';
    import Overflow1 from '$lib/assets/icons/overflow-1.svg?component';
    import Overflow2 from '$lib/assets/icons/overflow-2.svg?component';
    import {PolarArea} from 'svelte-chartjs';
    import {Chart, registerables} from 'chart.js';
    import {getContext} from 'svelte';
    import {observeToggle} from '$lib/theme-toggle.js';

    Chart.register(...registerables);

    export let ctList;
    export let displayClass = '';
    export let displayHeader = true;
    export let displayHistory = true;
    export let headerText = 'Average distribution';

    let polarAreaChart, dailyAverage;

    const currentGoal = getContext('currentGoal');

    /** @type Array<FoodCategory> */
    const foodCategories = getContext('foodCategories');

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const refreshChart = (entries) => {
        polarAreaChart = getData(entries);

        dailyAverage = getAverageDailyIntake(entries);
    }

    $: ctList, refreshChart(ctList);

    observeToggle(document.documentElement, () => {
        refreshChart(ctList);
    });

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const getData = (entries) => {
        const style = getComputedStyle(document.body);
        const elemHtmlClasses = document.documentElement.classList;

        let borderColor = style.getPropertyValue('--color-surface-200');
        let labelColor = displayHistory ? style.getPropertyValue('--color-surface-100') : style.getPropertyValue('--color-surface-50')
        let labelTextColor = style.getPropertyValue('--color-surface-900')

        if (elemHtmlClasses.contains('dark')) {
            borderColor = style.getPropertyValue('--color-surface-500');
            labelColor = displayHistory ? style.getPropertyValue('--color-surface-800') : style.getPropertyValue('--color-surface-900');
            labelTextColor = style.getPropertyValue('--color-surface-100');
        }

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
            chartData: {
                labels: labels,
                datasets: [{
                    label: '∅ kcal',
                    data: values,
                    hoverOffset: 4,
                    backgroundColor: [
                        `rgb(${style.getPropertyValue('--color-primary-500')} / .7)`,
                        `rgb(${style.getPropertyValue('--color-secondary-500')} / .7)`,
                        `rgb(${style.getPropertyValue('--color-tertiary-500')} / .7)`,
                        `rgb(${style.getPropertyValue('--color-warning-500')} / .7)`,
                        `rgb(${style.getPropertyValue('--color-error-500')} / .7)`
                    ],
                    borderColor: `rgb(${borderColor})`
                }]
            },
            chartOptions: {
                plugins: {
                    title: {
                        display: false,
                        align: 'center',
                        text: 'Last 7 days'
                    },
                    legend: {
                        align: 'center',
                        labels: {
                            color: `rgb(${labelTextColor})`
                        }
                    }
                },
                scales: {
                    r: {
                        ticks: {
                            backdropColor: `rgb(${labelColor})`,
                            color: `rgb(${labelTextColor})`
                        }
                    }
                }
            }
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
                        <div class="text-right">
                            &empty; target intake:
                        </div>
                        <div class="text-left">
                            ~{$currentGoal.targetCalories}kcal
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