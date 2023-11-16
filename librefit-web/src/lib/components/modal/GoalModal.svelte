<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import { getDateAsStr } from '$lib/util';

    const today = new Date();

    const modalStore = getModalStore();

    /** @type Goal */
    let goal = {
        added: getDateAsStr(today),
        endDate: getDateAsStr(new Date(today.getFullYear() + 1, today.getMonth(), today.getDate())),
        startDate: getDateAsStr(today),
        initialWeight: 0,
        targetWeight: 0
    }

    const onSubmit = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                goal: goal
            });
        }
    }

    const onCancel = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                cancelled: true
            });
        }
    }
</script>

<div class="modal block bg-surface-100-800-token w-modal h-auto p-4 space-y-4 rounded-container-token shadow-xl">
    <header class="text-2xl font-bold">
        Set goal
    </header>

    <div>
        <form class="variant-ringed p-4 space-y-4 rounded-container-token">
            <ValidatedInput
                    name="startDate"
                    type="date"
                    label="Begin"
                    required
                    bind:value={goal.startDate}
            />

            <ValidatedInput
                    name="initialWeight"
                    type="number"
                    label="Starting weight"
                    required
                    bind:value={goal.initialWeight}
            />

            <ValidatedInput
                    name="endDate"
                    type="date"
                    label="End"
                    required
                    bind:value={goal.endDate}
            />

            <ValidatedInput
                    name="targetWeight"
                    type="number"
                    label="Target weight"
                    required
                    bind:value={goal.targetWeight}
            />
        </form>
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