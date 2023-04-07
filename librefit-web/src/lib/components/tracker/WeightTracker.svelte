<script>
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import {getDateAsStr} from '$lib/util.js';

    export let trackerEntries;

    let addValue = 0;
    let sequence = 1;

    const date = getDateAsStr(new Date());

    const addEntry = () => {

    }

    const updateEntry = () => {

    }

    const deleteEntry = () => {

    }
</script>

<div class="flex flex-col grow gap-4">
    <TrackerInput
            bind:value={addValue}
            dateStr={date}
            bind:id={sequence}
            on:add={addEntry}
            unit={'kg'}
    />
    {#each trackerEntries as trackerInput}
        <TrackerInput
                disabled={true}
                existing={true}
                value={trackerInput.amount}
                category={trackerInput.category}
                dateStr={trackerInput.added}
                id={trackerInput.id}
                on:add={addEntry}
                on:update={updateEntry}
                on:remove={deleteEntry}
                unit={'kg'}
        />
    {/each}
</div>