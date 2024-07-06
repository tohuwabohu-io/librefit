<script>
    import {getModalStore} from '@skeletonlabs/skeleton';
    import {convertDateStrToDisplayDateStr} from '$lib/date.js';
    import TrackerInput from '$lib/components/TrackerInput.svelte';

    let value;

    const modalStore = getModalStore();

    /** @type Array<WeightTrackerEntry> */
    let weightList;

    if ($modalStore[0] && $modalStore[0].meta) {
        weightList = $modalStore[0].meta.weightList;
    }

    const onSubmit = (eventType, e) => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                detail: {
                    type: eventType,
                    close: true,
                    detail: e.detail
                }
            });
        }
    }

    const onCancel = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response(undefined);
        }
    }
</script>

<div class="modal block bg-surface-100-800-token w-modal h-auto p-4 space-y-4 rounded-container-token shadow-xl">
    <header class="text-2xl font-bold">
        Update weight for {convertDateStrToDisplayDateStr(weightList[0].added)}
    </header>

    {#each weightList as entry}
    <TrackerInput compact={true}
        value={entry.amount}
        dateStr={entry.added}
        sequence={entry.sequence}
        category={entry.category}
        on:update={(e) => onSubmit('update', e)}
        on:remove={(e) => onSubmit('remove', e)}
        existing={entry.sequence !== undefined}
        disabled={entry.sequence !== undefined}
        unit={'kg'}
    />
    {/each}

    <footer class="modal-footer flex justify-end space-x-2">

        <button on:click|preventDefault={onCancel} class="btn variant-ringed">
            Cancel
        </button>

        <button on:click|preventDefault={onSubmit} class="btn variant-filled">
            Submit
        </button>
    </footer>
</div>