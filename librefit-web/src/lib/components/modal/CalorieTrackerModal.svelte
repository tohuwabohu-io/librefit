<script>
    import {getModalStore} from '@skeletonlabs/skeleton';
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import {display_date_format, getDateAsStr, getDaytimeFoodCategory} from '$lib/date.js';

    const modalStore = getModalStore();

    /** @type Array<CalorieTrackerEntry> */
    export let entries;

    /** @type Array<FoodCategory> */
    export let categories;

    if ($modalStore[0] && $modalStore[0].meta) {
        entries = $modalStore[0].meta.entries;
        categories = $modalStore[0].meta.categories;
    }

    const onSubmit = (eventType, event) => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                detail: {
                    type: eventType,
                    close: true,
                    detail: event.detail
                }
            });
        }
    }

    const onCancel = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                detail: {
                    close: true
                }
            });
        }
    }
</script>

<div class="modal block bg-surface-100-800-token w-modal h-auto p-4 space-y-4 rounded-container-token shadow-xl">
    {#if !entries}
        <header class="text-2xl font-bold">
            Add entry for {getDateAsStr(new Date(), display_date_format)}
        </header>
        <div>
            <TrackerInput categories={categories}
                          dateStr={getDateAsStr(new Date())}
                          category={getDaytimeFoodCategory(new Date())}
                          unit={'kcal'}
                          on:add={(e) => onSubmit('add', e)}
            />
        </div>
    {:else}
        <header class="text-2xl font-bold">
            Edit tracker for {getDateAsStr(new Date(), display_date_format)}
        </header>
        <div class="flex flex-col grow gap-4 justify-between">
            {#each entries as entry}
            <TrackerInput {categories}
                          value={entry.amount}
                          dateStr={entry.added}
                          sequence={entry.sequence}
                          category={entry.category}
                          on:add={(e) => onSubmit('add', e)}
                          on:update={(e) => onSubmit('update', e)}
                          on:remove={(e) => onSubmit('remove', e)}
                          existing={entry.sequence !== undefined}
                          disabled={entry.sequence !== undefined}
                          unit={'kcal'}
            />
            {/each}
        </div>
    {/if}

    <footer class="modal-footer flex justify-start space-x-2">
        <button on:click|preventDefault={onCancel} class="btn variant-ringed">
            Close
        </button>
    </footer>
</div>