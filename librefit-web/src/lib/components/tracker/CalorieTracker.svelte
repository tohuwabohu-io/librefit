<script>
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import {createEventDispatcher} from 'svelte';
    import {getDateAsStr} from '$lib/util.js';

    export let entries;
    export let categories;

    const dispatch = createEventDispatcher();
    const today = new Date();
    const todayDateStr = getDateAsStr(new Date());

    const addCalories = (e) => {
        dispatch('addCalories', {
            sequence: getLastSequence(),
            date: e.detail.date,
            value: e.detail.value,
            category: e.detail.category
        });
    }

    const updateCalories = (e) => {
        dispatch('updateCalories', {
            sequence: e.detail.sequence,
            date: e.detail.date,
            value: e.detail.value,
            category: e.detail.category
        })
    }

    const deleteCalories = (e) => {
        dispatch('deleteCalories', {
            sequence: e.detail.sequence,
            date: e.detail.date
        })
    }

    const getLastSequence = () => {
        return Math.max(...entries.filter(entry => entry.added === todayDateStr).map(entry => entry.id)) + 1;
    }
</script>

<TrackerRadial entries={entries.map(e => e.amount)} />
<div class="flex flex-col grow gap-4">
    {#each entries as entry}
        <TrackerInput {categories}
                      value={entry.amount}
                      dateStr={entry.added}
                      id={entry.id}
                      category={entry.category}
                      on:add={addCalories}
                      on:update={updateCalories}
                      on:remove={deleteCalories}
                      existing={entry.id !== undefined}
                      disabled={entry.id !== undefined}
                      unit={'kcal'}
        />
    {/each}
</div>