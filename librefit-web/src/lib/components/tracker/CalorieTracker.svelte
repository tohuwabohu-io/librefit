<script>
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import {createEventDispatcher} from 'svelte';
    import {getDateAsStr} from '$lib/util.js';

    export let entries;
    export let categories;

    const dispatch = createEventDispatcher();

    const addCalories = (e) => {
        dispatch('addCalories', {
            date: e.detail.date,
            value: e.detail.value,
            category: e.detail.category,
            target: e.detail.target,
            callback: e.detail.callback
        });
    }

    const updateCalories = (e) => {
        dispatch('updateCalories', {
            sequence: e.detail.sequence,
            date: e.detail.date,
            value: e.detail.value,
            category: e.detail.category,
            target: e.detail.target,
            callback: e.detail.callback
        })
    }

    const deleteCalories = (e) => {
        dispatch('deleteCalories', {
            sequence: e.detail.sequence,
            date: e.detail.date,
            target: e.detail.target,
            callback: e.detail.callback
        })
    }
</script>

<div class="flex lg:flex-row flex-col grow gap-4">
    <div class="self-center">
        <TrackerRadial entries={entries.map(e => e.amount)} />
    </div>
    <div class="flex flex-col grow gap-4">
        {#each entries as entry}
            <TrackerInput {categories}
                          value={entry.amount}
                          dateStr={entry.added}
                          sequence={entry.sequence}
                          category={entry.category}
                          on:add={addCalories}
                          on:update={updateCalories}
                          on:remove={deleteCalories}
                          existing={entry.sequence !== undefined}
                          disabled={entry.sequence !== undefined}
                          unit={'kcal'}
            />
        {/each}
    </div>
</div>
