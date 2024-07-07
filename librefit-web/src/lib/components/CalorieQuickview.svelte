<script>
    import {Bar} from 'svelte-chartjs';
    import {createEventDispatcher} from 'svelte';
    import Target from '$lib/assets/icons/target-arrow.svg?component';
    import TargetOff from '$lib/assets/icons/target-off.svg?component';
    import Wand from '$lib/assets/icons/wand.svg?component';

    import {paintCalorieTrackerQuickview} from '$lib/quickview-chart.js';
    import {goto} from '$app/navigation';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import {observeToggle} from '$lib/theme-toggle.js';

    /** @type Array<CalorieTrackerEntry> */
    export let entries;

    /** @type Goal */
    export let currentGoal;

    export let displayClass = '';
    export let displayHeader = true;
    export let headerText = 'Target Quickview';

    const modalStore = getModalStore();
    const dispatch = createEventDispatcher();

    let targetButton;
    let quickview;

    if (entries && currentGoal) {
        quickview = paintCalorieTrackerQuickview(entries, currentGoal);
    }

    const setTarget = (event) => {
        dispatch('setTarget', {
            goal: event.goal,
            target: targetButton
        });
    }

    const onSetTarget = () => {
        modalStore.trigger({
            type: 'component',
            component: 'goalModal',
            meta: {
                /** @type Goal */
                goal: currentGoal
            },
            response: async (e) => {
                if (!e.cancelled) {
                    setTarget(e);
                }

                modalStore.close();
            }
        })
    }

    observeToggle(document.documentElement, () => {
        quickview = paintCalorieTrackerQuickview(entries, currentGoal);
    });
</script>


<div class="{displayClass} gap-4 text-center justify-between relative h-full">
    {#if displayHeader}<h2 class="h3">{headerText}</h2>{/if}

    {#if entries && currentGoal}
    <div class="flex flex-col xl:w-fit h-full justify-between gap-4">
        <Bar data={quickview.chartData} options={quickview.chartOptions}/>
    </div>

    {:else}
    <div class="flex flex-col gap-4 m-auto">
        <TargetOff width={100} height={100} class="self-center"/>
        <p>
            No target set up.
        </p>
    </div>
    {/if}

    <div class="flex">
        <div class="btn-group variant-filled w-fit grow">
            <button class="w-1/2" aria-label="add calories" on:click={onSetTarget} bind:this={targetButton}>
                <span>
                    <Target/>
                </span>
                <span>
                    Set target
                </span>
            </button>
            <button class="w-1/2" aria-label="edit calories"  on:click|preventDefault={() => goto('/wizard')}>
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