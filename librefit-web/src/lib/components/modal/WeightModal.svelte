<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';

    let value;

    const modalStore = getModalStore();

    const onSubmit = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                detail: {
                    value: value
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
        Update weight
    </header>

    <div>
        <ValidatedInput bind:value type="number" unit="kg" required/>
    </div>

    <footer class="modal-footer flex justify-end space-x-2">

        <button on:click|preventDefault={onCancel} class="btn variant-ringed">
            Cancel
        </button>

        <button on:click|preventDefault={onSubmit} class="btn variant-filled">
            Submit
        </button>
    </footer>
</div>