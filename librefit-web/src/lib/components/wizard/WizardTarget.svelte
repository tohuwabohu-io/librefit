<script>
    import {WizardRecommendation} from '$lib/model';
    import {WizardOptions} from '$lib/enum.ts';
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';

    /** @type {WizardResult} */
    export let calculationResult;

    /** @type {WizardInput} */
    export let calculationInput;

    export let chosenOption;

    const activeClass = 'variant-ringed-primary';

    let activeElement;
    let anchorDefault, anchorRecommendation, anchorCustomWeight, anchorCustomDate, anchorCustom;

    /**
     * @param a {HTMLAnchorElement}
     * @param param {WizardOptions}
     */
    const choose = (a, param) => {
        if (activeElement) activeElement.classList.remove(activeClass);

        activeElement = a;
        activeElement.classList.add(activeClass);

        chosenOption.userChoice = param;
    }
</script>

<h2 class="h2">Next steps</h2>
<p>
    Let me assist you with a few suggestions. I created a set of targets for you to choose depending on what
    you want to achieve. You can also set your own, if preferred.
</p>
<div class="flex flex-col gap-4">
    <a class="block card card-hover p-4" bind:this={anchorDefault}
       on:click|preventDefault={() => choose(anchorDefault, WizardOptions.Default)} href="javascript.void(0)"
    >
        <h3 class="h3">Take my initial values.</h3>
        <p>
            I'm fine with what you presented before, set the weight {calculationInput.calculationGoal.toLowerCase() }
            target based on my input.
        </p>
    </a>

    <a class="block card card-hover p-4" bind:this={anchorRecommendation}
       on:click|preventDefault={() => choose(anchorRecommendation, WizardOptions.Recommended)} href="javascript.void(0)"
    >
        <h3 class="h3">I'll take your recommendation.</h3>
        <p>
            I want to {calculationResult.recommendation.toLowerCase()} {calculationResult.recommendation === WizardRecommendation.Hold ? 'my' : 'some'}  weight. Create a target for that.
        </p>
    </a>

    <a class="block card card-hover p-4" bind:this={anchorCustomWeight}
       on:click|preventDefault={() => choose(anchorCustomWeight, WizardOptions.Custom_weight)} href="javascript.void(0)"
    >
        <h3 class="h3">I want to reach my dream weight.</h3>
        <p>
            How can I get to my target weight as fast as possible?
        </p>
        <ValidatedInput bind:value={chosenOption.customDetails}
                        type="number"
                        label="Target weight"
                        unit={'kg'}
        />
    </a>

    {#if calculationResult.recommendation !== WizardRecommendation.Hold}
    <a class="block card card-hover p-4" bind:this={anchorCustomDate}
       on:click|preventDefault={() => choose(anchorCustomDate, WizardOptions.Custom_date)} href="javascript.void(0)"
    >
        <h3 class="h3">I am ready to commit.</h3>
        <p>
            How much weight {calculationInput.calculationGoal.toLowerCase()} can I achieve until a specific date?
        </p>
        <ValidatedInput bind:value={chosenOption.customDetails}
                        type="date"
                        label="Target date"
        />
    </a>
    {/if}

    <a class="block card card-hover p-4 flex-grow" bind:this={anchorCustom}
       on:click|preventDefault={() => choose(anchorCustom, WizardOptions.Custom)} href="javascript.void(0)"
    >
        <h3 class="h3">I want to set a custom target.</h3>

        <p>
            I understand the implications of what was presented to me and know what I am doing.
        </p>
    </a>
</div>
