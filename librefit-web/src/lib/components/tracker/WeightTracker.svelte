<script>
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/util.js';
    import { createEventDispatcher } from 'svelte';
    import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';

    export let entries;
    export let lastEntry;
    export let goal;
    export let initialAmount = 0;

    const todayDateStr = getDateAsStr(new Date());

    const dispatch = createEventDispatcher();
    let sequence = 1;

    const addWeight = (e) => {
        dispatch('addWeight', {
            sequence: getLastSequence(),
            date: todayDateStr,
            value: e.detail.value
        });
    }

    const updateWeight = (e) => {
        dispatch('updateWeight', {
            sequence: e.detail.sequence,
            date: e.detail.date,
            value: e.detail.value
        });
    }

    const deleteWeight = (e) => {
        dispatch('deleteWeight', {
            sequence: e.detail.sequence,
            date: e.detail.date
        });
    }

    const getLastSequence = () => {
        return Math.max(...entries.filter(entry => entry.added === todayDateStr).map(entry => entry.id)) + 1;
    }

</script>

<div class="flex flex-col grow gap-4">
    <div class="flex flex-col gap-4 justify-between">
        <div class="flex flex-col gap-4 text-center items-center">
            {#if !lastEntry}
                <NoScale width={100} height={100} />

                <p>
                    Nothing tracked yet. Today is a good day to start!
                </p>
            {:else}
                <Scale width={100} height={100} />

                <p>
                    Current weight: {lastEntry.amount}kg ({convertDateStrToDisplayDateStr(lastEntry.added)})
                </p>
            {/if}

            {#if !goal}
                <p>
                    No goal set up.
                </p>
            {:else}
                <p>
                    Goal: {goal.endAmount}kg @ ({convertDateStrToDisplayDateStr(goal.endDate)})
                </p>
            {/if}

            <div class="btn-group variant-filled-primary">
                <button>
                    Update weight
                </button>
                <button>
                    Update goal
                </button>
            </div>
        </div>

        {#if entries}
            <div class="flex flex-col grow gap-4">
                <Accordion>
                    <AccordionItem>
                        <svelte:fragment slot="summary">History</svelte:fragment>
                        <svelte:fragment slot="content">
                        {#each entries as trackerInput}
                            <TrackerInput
                                    disabled={true}
                                    existing={true}
                                    value={trackerInput.amount}
                                    category={trackerInput.category}
                                    dateStr={trackerInput.added}
                                    id={trackerInput.id}
                                    on:add={addWeight}
                                    on:update={updateWeight}
                                    on:remove={deleteWeight}
                                    unit= {'kg'}
                            />
                        {/each}
                        </svelte:fragment>
                    </AccordionItem>
                </Accordion>
            </div>
        {/if}
    </div>
</div>