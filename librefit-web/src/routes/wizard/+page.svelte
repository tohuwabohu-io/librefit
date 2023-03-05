<script lang="ts">
    import {
        CalculationGoal,
        CalculationSex,
        Configuration,
        Tdee,
        TdeeResourceApi
    } from 'librefit-api/rest';
    import {RadioGroup, RadioItem} from '@skeletonlabs/skeleton';

    /** @type {import('./$types').ActionData} */ export let form;

    let step = 1;
    let tdee: Tdee = {
        age: 30,
        height: 160,
        sex: CalculationSex.Female,
        weight: 60,
        activityLevel: 1.25,
        calculationGoal: CalculationGoal.Loss,
        weeklyDifference: 3
    };

    if (form) {
        tdee = form;
    }

    const activityLevels = [
        {label: 'Level 1: Mostly Sedentary', value: 1},
        {label: 'Level 2: Light Activity', value: 1.25},
        {label: 'Level 3: Moderate Activity', value: 1.5},
        {label: 'Level 4: Highly Active', value: 1.75},
        {label: 'Level 5: Professional Athlete', value: 2}
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
    const calculate = async (e: Event) => {
        e.preventDefault();

        await tdeeApi
            .tdeeCalculateAgeSexWeightHeightActivityLevelDiffGainGet({
                age: tdee.age,
                activityLevel: tdee.activityLevel,
                diff: tdee.weeklyDifference,
                gain: tdee.calculationGoal,
                sex: tdee.sex,
                height: tdee.height,
                weight: tdee.weight
            })
            .catch((reason) => console.error(reason));
    };
</script>

<section>
    <div class="container mx-auto p-8 space-y-8">
        <form method="POST">
            <!-- {#if step === 1} -->
            <h1>Wizard</h1>

            <p>
                To find the optimal amount of how many calories you should consume per day to reach a
                specific goal, it's a good idea to calculate your TDEE.
            </p>

            <p>
                In order to do so, please provide some necessary information about yourself to make the
                calculation work.
            </p>

            <h2>Step 1</h2>

            <label class="label" id="lblAge">
                <span>Age</span>
                <input class="input" type="text" bind:value={tdee.age} disabled aria-labelledby="lblAge"/>
                <input class="input" type="range" name="age" bind:value={tdee.age} min="15" max="99" aria-labelledby="lblAge"/>
            </label>

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

            <label class="label" id="lblHeight">
                <span>Height in cm</span>
                <input class="input" type="text" bind:value={tdee.height} disabled aria-labelledby="lblHeight"/>
                <input type="range" name="height" bind:value={tdee.height} min="100" max="220" aria-labelledby="lblHeight"/>
            </label>

            <!-- description="Your height in cm." -->

            <label class="label" id="lblWeight">
                <span>Weight</span>
                <input class="input" type="text" bind:value={tdee.weight} disabled aria-labelledby="lblWeight"/>
                <input type="range" name="weight" bind:value={tdee.weight} min="30" max="300" aria-labelledby="lblWeight"/>
            </label>

            <!--description="Your current weight in kg."-->
            <!--{/if} -->

            <h2>Step 2</h2>

            <p>
                How active are you during your day? Please choose what describes your daily activity level
                best.
            </p>

            <label class="label">
                <span>Activity Level</span>

                <RadioGroup class="btn-group-vertical">
                    {#each activityLevels as activityLevel}
                        <RadioItem
                                bind:group={tdee.activityLevel}
                                name="activityLevel"
                                value={activityLevel.value}>
                            {activityLevel.label}
                        </RadioItem>
                    {/each}
                </RadioGroup>
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

            <label class="label">
                <span>Gain/Loss</span>
                <input type="range" name="diff" bind:value={tdee.weeklyDifference} min="0" max="7"/>
            </label>

            <p>{tdee.weeklyDifference / 10}kg</p>

            <button class="btn">Confirm</button>
        </form>

        {#if form}
            <h2>Your result</h2>

            <p>
                Based on your input, your basic metabolic rate is {tdee.bmr}kcal. Your daily calorie
                consumption to hold your weight should be around {tdee.tdee}kcal.
            </p>
        {/if}

        <button class="btn" on:click={previousStep}>Previous</button>
        <button class="btn" on:click={nextStep}>Next</button>
    </div>
</section>
