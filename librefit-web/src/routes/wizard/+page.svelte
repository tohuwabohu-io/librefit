<script>
    import TdeeStepper from '$lib/components/TdeeStepper.svelte';
    import { getModalStore, getToastStore } from '@skeletonlabs/skeleton';
    import { showToastError, showToastSuccess, showToastWarning } from '$lib/toast.js';
    import { getContext } from 'svelte';
    import {
        calculateForTargetDate,
        calculateForTargetWeight,
        calculateTdee,
        createTargetDateTargets,
        createTargetWeightTargets,
        postWizardResult,
        validateCustomDate,
        validateCustomWeight
    } from '$lib/api/wizard.js';
    import WizardResult from '$lib/components/wizard/WizardResult.svelte';
    import WizardTarget from '$lib/components/wizard/WizardTarget.svelte';
    import { addDays } from 'date-fns';
    import { getDateAsStr } from '$lib/date.js';
    import { WizardOptions } from '$lib/enum.js';
    import { WizardRecommendation } from '$lib/api/model.js';

    const toastStore = getToastStore();
    const modalStore = getModalStore();

    const indicator = getContext('indicator');
    const user = getContext('user');

    /** @type {WizardResult} */
    let calculationResult;

    /** @type {WizardInput} */
    let calculationInput;

    let calculationError;

    let chosenOption = {
        userChoice: undefined,
        customDetails: undefined
    };

    const today = new Date();

    const calculate = async (e) => {
        $indicator = $indicator.start();

        calculationInput = e.detail.input;

        await calculateTdee(calculationInput).then(async response => {
            calculationResult = response;
        }).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
    }

    const calculateCustomDate = async (wizardDetails) => {
        const validation = validateCustomDate({
            value: wizardDetails.targetDate
        });

        if (!validation.valid) {
            showToastWarning(toastStore, validation.errorMessage)
        } else {
            $indicator = $indicator.start();

            await calculateForTargetDate({
                age: calculationInput.age,
                height: calculationInput.height,
                weight: calculationInput.weight,
                sex: calculationInput.sex,
                targetDate: wizardDetails.targetDate,
                calculationGoal: calculationInput.calculationGoal
            }).then(/** @type WizardTargetDateResult */ customWizardResult => {
                const targetsByRate = createTargetDateTargets(calculationInput, calculationResult, customWizardResult, today, wizardDetails.targetDate);

                showModal({
                    targetsByRate: targetsByRate
                });
            }).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
        }

    }

    const calculateCustomWeight = async (wizardDetails) => {
        const validation = validateCustomWeight({
            value: wizardDetails.targetWeight
        });

        if (!validation.valid) {
            showToastWarning(toastStore, validation.errorMessage)
        } else {
            $indicator = $indicator.start();

            await calculateForTargetWeight({
                age: calculationInput.age,
                height: calculationInput.height,
                weight: calculationInput.weight,
                sex: calculationInput.sex,
                targetWeight: wizardDetails.targetWeight
            }).then(/** @type WizardTargetWeightResult */ customWizardResult => {
                const targetsByRate = createTargetWeightTargets(calculationInput, calculationResult, customWizardResult, today, wizardDetails.targetWeight);

                showModal({
                    targetsByRate: targetsByRate,
                    warningMessage: customWizardResult.message
                });
            }).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
        }

    }

    const reset = () => {
        calculationResult = undefined;
        calculationError = false;
        chosenOption.userChoice = undefined;
        chosenOption.customDetails = undefined;
    }

    /** @param {WizardResult} calculationResult */
    const processResult =  async (calculationResult) => {
        const endDate = addDays(today, calculationResult.durationDays);

        const wizardDetails = {
            /** @type CalorieTarget */
            calorieTarget: {
                added: getDateAsStr(today),
                endDate: getDateAsStr(endDate),
                startDate: getDateAsStr(today),
            },

            /** @type WeightTarget */
            weightTarget: {
                added: getDateAsStr(today),
                endDate: getDateAsStr(endDate),
                startDate: getDateAsStr(today),
                initialWeight: calculationInput.weight
            }
        }

        if (chosenOption.userChoice === WizardOptions.Default) {
            wizardDetails.calorieTarget.targetCalories = calculationResult.target;
            wizardDetails.calorieTarget.maximumCalories = calculationResult.tdee;

            wizardDetails.weightTarget.targetWeight = calculationResult.targetWeight;

            showModal(wizardDetails);
        } else if (chosenOption.userChoice === WizardOptions.Recommended) {
            if (calculationResult.recommendation === WizardRecommendation.Hold) {
                wizardDetails.calorieTarget.targetCalories = calculationResult.tdee;
                wizardDetails.calorieTarget.maximumCalories = calculationResult.tdee;

                wizardDetails.weightTarget.targetWeight = calculationInput.weight;

                showModal(wizardDetails);
            } else {
                wizardDetails.calorieTarget.targetCalories = calculationResult.target;
                wizardDetails.calorieTarget.maximumCalories = calculationResult.tdee;

                // calculate for recommendation
                await calculateCustomWeight({
                    targetWeight: calculationResult.recommendation === WizardRecommendation.Gain
                        ? calculationResult.targetWeightLower : calculationResult.targetWeightUpper
                });
            }
        } else if (chosenOption.userChoice === WizardOptions.Custom_date) {
            // calculate with custom weight input
            await calculateCustomDate({targetDate: chosenOption.customDetails});
        } else if (chosenOption.userChoice === WizardOptions.Custom_weight) {
            // calculate with custom date input
            await calculateCustomWeight({targetWeight: chosenOption.customDetails});
        } else if (chosenOption.userChoice === WizardOptions.Custom) {
            showModal(wizardDetails);
        }
    }

    const showModal = (wizardDetails) => {
        modalStore.trigger({
            type: 'component',
            component: 'targetModal',
            meta: wizardDetails,
            response: async (e) => {
                if (e && !e.cancelled) {
                    await createTargetsAddWeight(e);
                }

                modalStore.close();
            }
        })
    }


    const createTargetsAddWeight = async (detail) => {
        $indicator = $indicator.start();

        /** @type {Wizard} */
        const wizard = {
            calorieTarget: detail.calorieTarget,
            weightTarget: detail.weightTarget,
            weightTracker: {
                added: getDateAsStr(today),
                sequence: 1,
                amount: detail.weightTarget.initialWeight
            }
        }

        await postWizardResult(wizard).then(async _ => showToastSuccess(toastStore, 'Successfully saved your targets.'))
            .catch((error) => showToastError(toastStore, error))
            .finally(() => $indicator = $indicator.finish());
    }
</script>

<svelte:head>
    <title>LibreFit - TDEE Wizard</title>
</svelte:head>

<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1 class="h1">TDEE Calculator</h1>

        {#if !calculationResult && !calculationError}
            <TdeeStepper on:calculate={calculate}/>
        {:else if !calculationError}
            <WizardResult {calculationResult}
                          {calculationInput}
            />

            <WizardTarget {calculationResult}
                          {calculationInput}
                          bind:chosenOption={chosenOption}
                          on:setTargets={createTargetsAddWeight}
            />

            <div class="flex flex-grow justify-between">
                <button on:click|preventDefault={reset} class="btn variant-filled">Recalculate</button>
                <button on:click|preventDefault={() => processResult(calculationResult)}
                        class="btn variant-filled-primary"
                        disabled={chosenOption.userChoice === undefined}
                >
                    Review
                </button>
            </div>
        {:else}
            <p>
                An error occurred. Please try again later.

                <button on:click|preventDefault={reset} class="btn variant-filled">Recalculate</button>
            </p>
        {/if}
    </div>
</section>