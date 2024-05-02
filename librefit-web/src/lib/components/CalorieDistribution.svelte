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
    export let displayHistory = true;

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

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const getData = (entries) => {
        const style = getComputedStyle(document.body);
        const elemHtmlClasses = document.documentElement.classList;

        let borderColor = style.getPropertyValue('--color-surface-200');
        let labelColor = style.getPropertyValue('--color-surface-100');
        let labelTextColor = style.getPropertyValue('--color-surface-900')

        if (elemHtmlClasses.contains('dark')) {
            borderColor = style.getPropertyValue('--color-surface-500');
            labelColor = style.getPropertyValue('--color-surface-800');
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
                        pointLabels: {
                            font: {
                                size: 100
                            }
                        },
                        ticks: {
                            backdropColor: `rgb(${labelColor})`,
                            color: `rgb(${labelTextColor})`
                        },
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

<div class="{displayClass} p-4 text-center justify-between ">
    {#if ctList}
        {#if displayHeader}<h3 class="h3">Average distribution</h3>{/if}

        <PolarArea data={polarAreaChart.chartData} options={polarAreaChart.chartOptions}/>

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

        {#if displayHistory}
            <button class="btn variant-filled" on:click|preventDefault={() => goto('/tracker/calories')}>Show history
            </button>
        {/if}
    {/if}
</div>