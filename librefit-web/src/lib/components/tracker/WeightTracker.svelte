<script>
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import {getDateAsStr} from '$lib/util.js';
    import { createEventDispatcher } from 'svelte';

    export let entries;
    export let firstTime = false;

    let addValue = 0;
    let sequence = 1;

    const todayDateStr = getDateAsStr(new Date());
    const dispatch = createEventDispatcher();

    const addWeight = (e) => {
        dispatch('addWeight', {
            sequence, addValue, todayDateStr
        });
    }

    const updateWeight = (e) => {
        dispatch('updateWeight', {
            sequence: e.detail.id,
            date: e.detail.dateStr,
            value: e.detail.value
        });
    }

    const deleteWeight = (e) => {
        dispatch('deleteWeight', {
            sequence: e.detail.id,
            date: e.detail.dateStr
        });
    }


</script>

<div class="flex flex-col grow gap-4">
    <div class="flex gap-4 justify-between">
        {#if entries.length <= 0}
            <NoScale width={100} height={100} />
        {:else}
            <Scale width={100} height={100} />
        {/if}

        <div class="flex flex-col grow gap-4">
            {#if entries.length <= 0}
                {#if firstTime}
                    <p>
                        Seems like you have not tracked anything so far. Today is the best day to start!
                    </p>
                {/if}
            {/if}

            <TrackerInput
                    bind:value={addValue}
                    dateStr={todayDateStr}
                    bind:id={sequence}
                    on:add={addWeight}
                    unit={'kg'}
            />
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
        </div>
    </div>
</div>