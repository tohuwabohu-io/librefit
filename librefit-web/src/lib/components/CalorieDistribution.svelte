<script>
    import {goto} from '$app/navigation';
    import Overflow1 from '$lib/assets/icons/overflow-1.svg?component';
    import Overflow2 from '$lib/assets/icons/overflow-2.svg?component';
    import Check from '$lib/assets/icons/check.svg?component';
    import {PolarArea} from 'svelte-chartjs';
    import {Category} from '$lib/api/model.js';
    import {Chart, registerables} from 'chart.js';
    import {getContext} from 'svelte';

    Chart.register(...registerables);

    export let data;
    export let displayClass = '';

    const currentGoal = getContext('currentGoal');

    /**
     * @param {Array<CalorieTrackerEntry>} entries
     */
    const getData = (entries) => {
        const labels = [];
        const values = [];

        const averageCategoryIntake = getAverageCategoryIntake(entries)

        if (averageCategoryIntake != null) {
            for (let cat of Object.keys(Category)) {
                const averageIntake = averageCategoryIntake.get(cat);

                if (averageIntake > 0) {
                    values.push(averageCategoryIntake.get(cat));
                    labels.push(cat);
                }
            }
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

            for (let cat of Object.keys(Category)) {
                const catEntries = nonEmpty.filter(e => e.category === Category[cat]);

                if (catEntries.length > 0) {
                    const catSum = catEntries.map(e => e.amount).reduce((a, b) => a + b);

                    catMap.set(cat, Math.round(dailyAverage * (catSum / sum)));
                }

            }

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
        };
    }

</script>

<div class="{displayClass} p-4 text-center justify-between ">
    {#await data.listCt}
        <p>Loading...</p>
    {:then ctList}
        {@const data = getData(ctList)}
        {@const options = getConfig(data)}
        {@const dailyAverage = getAverageDailyIntake(ctList)}

        <h3 class="h3">Average distribution</h3>

        <PolarArea {options} {data}/>

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
    {/await}
</div>