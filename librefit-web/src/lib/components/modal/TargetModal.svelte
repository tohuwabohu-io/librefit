<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import {getDateAsStr} from '$lib/date.js';
    import {validateCalorieTarget, validateTrackerAmount, validateWeightTarget} from '$lib/validation.js';

    const today = new Date();

    /** @type {any} */
    let errors = {
        valid: true,
        calorieTarget: {
            targetCalories: {},
            maximumCalories: {}
        },
        weightTarget: {
            initialWeight: {},
            targetWeight: {}
        }
    }

    let errorEndDate = {
        valid: true
    };

    const modalStore = getModalStore();
    let calorieTarget;

    let weightTarget;
    let added = getDateAsStr(today);
    let startDate = getDateAsStr(today);

    let endDate = getDateAsStr(new Date(today.getFullYear() + 1, today.getMonth(), today.getDate()));

    if ($modalStore[0] && $modalStore[0].meta) {
        calorieTarget = $modalStore[0].meta.calorieTarget;
        weightTarget = $modalStore[0].meta.weightTarget;
    }

    const onSubmit = () => {
        if (calorieTarget) populateAndValidateTarget(calorieTarget, validateCalorieTarget, 'targetCalories', 'maximumCalories');
        if (weightTarget) populateAndValidateTarget(weightTarget, validateWeightTarget, 'initialWeight', 'targetWeight');

        errors.valid &&= errorEndDate.valid;

        if (errors.valid) {
            if ($modalStore[0].response) {
                $modalStore[0].response({
                    calorieTarget: calorieTarget,
                    weightTarget: weightTarget
                });
            }
        }
    }

    const populateAndValidateTarget = (target, validationFn, prop1, prop2) => {
        target.added = added;
        target.startDate = startDate;
        target.endDate = endDate;

        errors[target] = validationFn(target);
        errorEndDate = errors[target].endDate;

        errors.valid &&= errors[target][prop1].valid;
        errors.valid &&= errors[target][prop2].valid;
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
        Set target
    </header>

    <div>
        <form class="variant-ringed p-4 space-y-4 rounded-container-token">
            <ValidatedInput
                    name="startDate"
                    type="date"
                    label="Begin"
                    required
                    bind:value={startDate}
            />

            <ValidatedInput
                    name="endDate"
                    type="date"
                    label="End"
                    required
                    bind:value={endDate}
                    errorMessage={errorEndDate.errorMessage}
            />

            {#if weightTarget}
            <ValidatedInput
                    name="initialWeight"
                    type="number"
                    label="Starting weight"
                    required
                    unit="kg"
                    bind:value={weightTarget.initialWeight}
                    emptyMessage="Please enter a valid initial weight."
                    validateDetail={validateTrackerAmount}
                    errorMessage={errors.weightTarget.initialWeight.errorMessage}
            />

            <ValidatedInput
                    name="targetWeight"
                    type="number"
                    label="Target weight"
                    required
                    unit="kg"
                    bind:value={weightTarget.targetWeight}
                    emptyMessage="Please enter a valid target weight."
                    validateDetail={validateTrackerAmount}
                    errorMessage={errors.weightTarget.targetWeight.errorMessage}
            />
            {/if}

            {#if calorieTarget}
            <ValidatedInput
                    name="targetCalories"
                    type="number"
                    label="Target intake"
                    required
                    unit="kcal"
                    bind:value={calorieTarget.targetCalories}
                    emptyMessage="Please enter a valid target amount."
                    validateDetail={validateTrackerAmount}
                    errorMessage={errors.calorieTarget.targetCalories.errorMessage}
            />

            <ValidatedInput
                    name="maximumCalories"
                    type="number"
                    label="Maximum intake"
                    required
                    unit="kcal"
                    bind:value={calorieTarget.maximumCalories}
                    emptyMessage="Please enter a valid maximum amount."
                    validateDetail={validateTrackerAmount}
                    errorMessage={errors.calorieTarget.maximumCalories.errorMessage}
            />
            {/if}
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