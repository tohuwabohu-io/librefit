<script lang="ts">
    import {
        CalculationGoal,
        CalculationSex,
        Configuration,
        Tdee,
        TdeeResourceApi
    } from 'librefit-api/rest';
    import {RadioGroup, RadioItem, RangeSlider} from '@skeletonlabs/skeleton';

    /** @type {import('./$types').ActionData} */ export let form;

    let step = 1;
    const exampleTdee: Tdee = {
        age: 30,
        height: 160,
        sex: CalculationSex.Female,
        weight: 60,
        activityLevel: 1.25,
        calculationGoal: CalculationGoal.Loss,
        weeklyDifference: 3
    };

    let tdee = exampleTdee;

    if (form) {
        tdee = form;
    }

    const activityLevels = [
        {label: 'Mostly Sedentary', value: 1},
        {label: 'Light Activity', value: 1.25},
        {label: 'Moderate Activity', value: 1.5},
        {label: 'Highly Active', value: 1.75},
        {label: 'Professional Athlete', value: 2}
    ];

    const sexes = [
        {label: 'Female', value: CalculationSex.Female},
        {label: 'Male', value: CalculationSex.Male}
    ];

    const goals = [
        {label: 'Weight Loss', value: CalculationGoal.Loss},
        {label: 'Weight Gain', value: CalculationGoal.Gain}
    ];

    const nextStep = (e: Event) => {
        e.preventDefault();

        if (step < 3) {
            step++;
        }
    };
    const previousStep = (e: Event) => {
        e.preventDefault();

        if (step > 1) {
            step--;
        }
    };
</script>

<svelte:head>
    <title>LibreFit - TDEE Wizard</title>
</svelte:head>

<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1>Wizard</h1>
        <form method="POST">
            <p>
                To find the optimal amount of how many calories you should consume per day to reach a
                specific goal, it's a good idea to calculate your TDEE.
            </p>

            <p>
                In order to do so, please provide some necessary information about yourself to make the
                calculation work.
            </p>

            <h2>Step 1</h2>

            <RangeSlider accent="accent-primary-500" name="age" bind:value={tdee.age} min={15} max={99}>Age</RangeSlider>
            <input class="input variant-ringed-secondary" type="text" bind:value={tdee.age} aria-label="Age"/>

            <label class="label">
                <span>Sex</span>

                <RadioGroup>
                    {#each sexes as sex}
                        <RadioItem bind:group={tdee.sex} value={sex.value} name="sex">
                            {sex.label}
                        </RadioItem>
                    {/each}
                </RadioGroup>
            </label>

            <RangeSlider accent="accent-primary-500" name="height" bind:value={tdee.height} min={100} max={220}>Height in cm</RangeSlider>
            <input class="input" type="text" bind:value={tdee.height} disabled aria-label="Height"/>

            <RangeSlider accent="accent-primary-500" name="weight" type="range" bind:value={tdee.weight} min={30} max={300}>Weight in kg</RangeSlider>
            <input class="input" type="text" bind:value={tdee.weight} disabled aria-label="Weight"/>

            <h2>Step 2</h2>

            <p>
                How active are you during your day? Please choose what describes your daily activity level
                best.
            </p>

            <label class="label">
                <span>Activity Level</span>

                <RadioGroup class="btn-group-vertical activity-level" rounded="rounded-xl">
                    {#each activityLevels as activityLevel}
                        <RadioItem
                                bind:group={tdee.activityLevel}
                                name="activityLevel"
                                value={activityLevel.value}>
                            {activityLevel.label}
                        </RadioItem>
                    {/each}
                </RadioGroup>

                <card class="card variant-ghost-tertiary p-4 text-center space-y-2">
                    lala land
                </card>
            </label>

            <h2>Step 3</h2>

            <p>Do you aim for weight loss or weight gain?</p>

            <label class="label">
                <span>Goal</span>

                <RadioGroup>
                    {#each goals as goal}
                        <RadioItem bind:group={tdee.calculationGoal} name="gain" value={goal.value}>
                            {goal.label}
                        </RadioItem>
                    {/each}
                </RadioGroup>
            </label>

            <p>How much weight are you looking to lose/gain per week?</p>

            <RangeSlider accent="accent-primary-500" name="diff" bind:value={tdee.weeklyDifference} min={0} max={7} ticked>Gain/Loss</RangeSlider>

            <p>{tdee.weeklyDifference / 10}kg</p>

            <button class="btn variant-ghost-primary">Confirm</button>
        </form>

        {#if form}
            <h2>Your result</h2>

            <p>
                Based on your input, your basic metabolic rate is {tdee.bmr}kcal. Your daily calorie
                consumption to hold your weight should be around {tdee.tdee}kcal.
            </p>
        {/if}

        <button class="btn variant-ghost" on:click={previousStep}>Previous</button>
        <button class="btn variant-ghost-primary" on:click={nextStep}>Next</button>
    </div>
</section>

<style>
</style>