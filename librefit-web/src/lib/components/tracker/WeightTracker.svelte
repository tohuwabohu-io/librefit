<script>
    import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import Target from '$lib/assets/icons/target-arrow.svg?component';
    import {createEventDispatcher} from 'svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/date.js';
    import TrackerInput from '$lib/components/TrackerInput.svelte';

    /**
     * @type Goal
     */
    export let currentGoal;

    /**
     * @type WeightTrackerEntry
     */
    export let lastEntry;

    let weightQuickAdd;

    const modalStore = getModalStore();
    const todayDateStr = getDateAsStr(new Date());
    const dispatch = createEventDispatcher();

    const addWeightQuickly = (e) => {
        dispatch('addWeight', {
            dateStr: todayDateStr,
            value: e.detail.value,
            callback: () => {
                e.detail.callback();
                weightQuickAdd = undefined;
            }
        });
    }

    const addWeight = (e) => {
        dispatch('addWeight', {
            dateStr: todayDateStr,
            value: e.detail.value
        });
    }

    const onEdit = (e) => {
        dispatch('updateWeight', {

        });
    }

    const onAdd = () => {
        modalStore.trigger({
            type: 'component',
            component: 'weightModal',
            response: (e) => {
                if (e) {
                    addWeight(e);
                }

                modalStore.close();
            }
        });
    }
</script>

<div class="flex flex-col grow gap-4 text-center items-center self-center w-full">
    {#if lastEntry}
        <p>
            Current weight: {lastEntry.amount}kg ({convertDateStrToDisplayDateStr(lastEntry.added)})
        </p>
    {:else}
        <p>
            Nothing tracked yet. Today is a good day to start!
        </p>
    {/if}

    {#if currentGoal}
        <p>
            Goal: {currentGoal.targetWeight}kg @ ({convertDateStrToDisplayDateStr(currentGoal.endDate)})
        </p>
    {:else}
        <p>
            No goal set up.
        </p>
    {/if}

    <div class="flex flex-col md:w-1/3 w-full gap-4">
        <TrackerInput
                bind:value={weightQuickAdd}
                on:add={addWeightQuickly}
                compact={true}
                unit={'kg'}
        />

        <div class="btn-group variant-filled grow">
            <button class="w-1/2" aria-label="add calories" on:click={onAdd}>
                <span>
                    <Scale/>
                </span>
                <span>
                    Add
                </span>
            </button>
            <button class="w-1/2" aria-label="edit calories" on:click={onEdit}>
                <span>
                    <Scale/>
                </span>
                <span>
                    Edit
                </span>
            </button>
        </div>
    </div>
</div>