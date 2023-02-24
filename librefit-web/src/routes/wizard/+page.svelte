<script>
    import {Container, Text, Title, NumberInput, NativeSelect, Button, Group, RadioGroup} from "@svelteuidev/core";

    /** @type {import('./$types').PageData} */  export let data;
    /** @type {import('./$types').ActionData} */  export let form;

    let step = 1;
    let tdee = {
        activityLevel: 1,
        bmr: 0
    }

    if (form) {
        tdee = form.tdee;
    }

    const activityLevels = [
        { label: 'Level 1: Mostly Sedentary', value: 1 },
        { label: 'Level 2: Light Activity', value: 1.25 },
        { label: 'Level 3: Moderate Activity', value: 1.50 },
        { label: 'Level 4: Highly Active', value: 1.75 },
        { label: 'Level 5: Professional Athlete', value: 2 }
    ];

    const goals = [
        { label: 'Weight Loss', value: 'l'},
        { label: 'Weight Gain', value: 'g' }
    ];

    let goal = 0;

    const nextStep = (e) => {
        e.preventDefault();

        if (step < 3) {
            step++;
        }
    }
    const previousStep = (e) => {
        e.preventDefault();

        if (step > 1) {
            step--;
        }
    }

</script>

<Container>
    <form method="POST">
        <!-- {#if step === 1} -->
        <Title ordering={1}>
            Wizard
        </Title>

        <Text>
            To find the optimal amount of how many calories you should consume per day to reach
            a specific goal, it's a good idea to calculate your TDEE.
        </Text>

        <Text>
            In order to do so, please provide some necessary information about yourself to
            make the calculation work.
        </Text>

        <Title ordering={2}>
            Step 1
        </Title>

        <NumberInput name="age" label="Age" bind:value={tdee.age}>
        </NumberInput>

        <NativeSelect name="sex" label="Sex" data="{['m', 'f']}" bind:value={tdee.sex}
                      placeholder="Pick one">

        </NativeSelect>

        <NumberInput name="height" label="Height" bind:value={tdee.height}
                     description="Your height in cm.">
        </NumberInput>

        <NumberInput name="weight" label="Weight" bind:value={tdee.weight}
                     description="Your current weight in kg.">

        </NumberInput>
        <!--{/if} -->

        <Title ordering={2}>
            Step 2
        </Title>

        <Text>
            How active are you during your day? Please choose what describes your daily activity
            level best.
        </Text>

        <RadioGroup name="activityLevel" items={activityLevels} bind:value={tdee.activityLevel}
                    itemDirection={'down'}>

        </RadioGroup>

        <Title ordering={2}>
            Step 3
        </Title>

        <Text>
            Do you aim for weight loss or weight gain?
        </Text>

        <RadioGroup items={goals}>

        </RadioGroup>

        <Text>
            How much weight are you looking to lose/gain per week?
        </Text>

        <div class="slider-container">
            <input class="slider" type="range" min="0" max="7" bind:value={goal}>
        </div>
        <Text>{goal / 10}kg</Text>

        <Button>Confirm</Button>
    </form>

    <Title ordering={2}>
        Your result
    </Title>

    {#if form?.status === 200}
    <Text>
        Based on your input, your basic metabolic rate is {form.tdee.bmr}kcal. Your daily calorie consumption to hold
        your weight should be around {form.tdee.tdee}kcal.
    </Text>
    {/if}

    <Button on:click={previousStep}>Previous</Button>
    <Button on:click={nextStep}>Next</Button>
</Container>

<style>
    .slider-container {
        width: 100%;
    }

    .slider {
        -webkit-appearance: none;
        height: 25px;
        background: #d3d3d3;
        outline: none;
        opacity: 0.7;
        -webkit-transition: .2s;
        transition: opacity .2s;
    }

    .slider:hover {
        opacity: 1;
    }

    .slider::-webkit-slider-thumb {
        -webkit-appearance: none;
        appearance: none;
        width: 25px;
        height: 25px;
        background: #04AA6D;
        cursor: pointer;
    }

    .slider::-moz-range-thumb {
        width: 25px;
        height: 25px;
        background: #04AA6D;
        cursor: pointer;
    }
</style>