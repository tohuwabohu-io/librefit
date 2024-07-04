<script>
    import {Bar} from 'svelte-chartjs';
    import Target from '$lib/assets/icons/target-arrow.svg?component';
    import Wand from '$lib/assets/icons/wand.svg?component';

    import {paintCalorieTrackerQuickview} from '$lib/quickview-chart.js';

    /** @type Array<CalorieTrackerEntry> */
    export let entries;

    /** @type Goal */
    export let currentGoal;

    export let displayClass = '';
    export let displayHeader = true;
    export let headerText = 'Weekly Quickview';

    const onSetTarget = () => {

    }

    const onOpenWizard = () => {

    }
</script>


<div class="{displayClass} gap-4 text-center justify-evenly relative">
    {#if displayHeader}<h2 class="h3">{headerText}</h2>{/if}

    {#if entries && currentGoal}
        {@const quickview = paintCalorieTrackerQuickview(entries, currentGoal)}
        <Bar data={quickview.chartData} options={quickview.chartOptions}/>
    {/if}

    <div class="flex">
        <div class="btn-group variant-filled w-fit grow">
            <button class="w-1/2" aria-label="add calories" on:click={onSetTarget}>
                <span>
                    <Target/>
                </span>
                <span>
                    Set target
                </span>
            </button>
            <button class="w-1/2" aria-label="edit calories" on:click={onOpenWizard}>
                <span>
                    <Wand/>
                </span>
                <span>
                    Wizard
                </span>
            </button>
        </div>
    </div>
</div>