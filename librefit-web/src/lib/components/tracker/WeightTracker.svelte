<script>
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import {convertDateStrToDisplayDateStr, getDateAsStr, parseStringAsDate} from '$lib/util.js';
    import { createEventDispatcher } from 'svelte';
    import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';
    import { modalStore } from '@skeletonlabs/skeleton';
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import * as dateUtil from 'date-fns';
    import {onMount} from 'svelte';

    export let entries;
    export let lastEntry;
    export let goal;

    const today = new Date();
    const todayDateStr = getDateAsStr(new Date());

    let dateFilterRange = {
        from: getDateAsStr(dateUtil.sub(today, { months: 1})),
        to: getDateAsStr(today)
    }

    const dispatch = createEventDispatcher();
    let sequence = 1;
    let entriesFiltered = [];

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

    const updateGoal = (e) => {
        console.log(e);

        dispatch('updateGoal', {
            goal: e.detail.goal
        })
    }

    const getLastSequence = () => {
        return Math.max(...entries.filter(entry => entry.added === todayDateStr).map(entry => entry.id)) + 1;
    }

    const showWeightModal = () => {
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

    const showGoalModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'goalModal',
            response: (e) => {
                if (e) {
                    updateGoal(e)
                }

                modalStore.close();
            }
        });
    }

    const filterEntries = (e) => {
        entriesFiltered = entries.filter((entry) => {
            const addedDate = parseStringAsDate(entry.added);
            const fromDate = parseStringAsDate(dateFilterRange.from);
            const toDate = parseStringAsDate(dateFilterRange.to);

            return toDate >= addedDate <= fromDate;
        })
    }

    onMount(async () => filterEntries())

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
                <button on:click|preventDefault={showWeightModal}>
                    Update weight
                </button>
                <button on:click|preventDefault={showGoalModal}>
                    Update goal
                </button>
            </div>
        </div>

        {#if entries && entries.length > 0}
            <div class="flex flex-col grow gap-4">
                <Accordion class="variant-ghost-surface rounded-xl">
                    <AccordionItem>
                        <svelte:fragment slot="summary">History</svelte:fragment>
                        <svelte:fragment slot="content">
                            {#if dateFilterRange}
                                <div class="flex gap-4">
                                    <ValidatedInput type="date" name="filterFrom" label="From" bind:value={dateFilterRange.from} />
                                    <ValidatedInput type="date" name="filterTo" label="To" bind:value={dateFilterRange.to} />

                                    <div class="self-center">
                                        <button class="btn variant-filled" on:click={filterEntries}>
                                            Filter
                                        </button>
                                    </div>
                                </div>
                            {/if}

                            {#each entriesFiltered as trackerInput}
                                <div class="flex flex-row gap-4 items-center">
                                    <div>
                                        <p>
                                            { convertDateStrToDisplayDateStr(trackerInput.added) }
                                        </p>
                                    </div>

                                    <div class="grow">
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
                                    </div>
                                </div>
                            {/each}
                        </svelte:fragment>
                    </AccordionItem>
                </Accordion>
            </div>
        {/if}
    </div>
</div>