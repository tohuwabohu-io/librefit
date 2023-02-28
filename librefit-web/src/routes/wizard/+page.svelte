<script lang="ts">
	import {Button, Container, NativeSelect, NumberInput, RadioGroup, Text, Title} from '@svelteuidev/core';
	import {CalculationGoal, CalculationSex, Tdee} from "librefit-api/rest";

	/** @type {import('./$types').ActionData} */ export let form;

	let step = 1;
	let tdee: Tdee = { age: 0, height: 0, sex: CalculationSex.Female, weight: 0, activityLevel: 1,
		calculationGoal: CalculationGoal.Loss,
		target: 0,
		weeklyDifference: 0
	}

	if (form) {
		tdee = form.tdee;
	}

	const activityLevels = [
		{ label: 'Level 1: Mostly Sedentary', value: 1 },
		{ label: 'Level 2: Light Activity', value: 1.25 },
		{ label: 'Level 3: Moderate Activity', value: 1.5 },
		{ label: 'Level 4: Highly Active', value: 1.75 },
		{ label: 'Level 5: Professional Athlete', value: 2 }
	];

	const sex = [ CalculationSex.Female, CalculationSex.Male ]

	const goals = [
		{ label: 'Weight Loss', value: CalculationGoal.Loss },
		{ label: 'Weight Gain', value: CalculationGoal.Gain }
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

<Container>
	<form method="POST">
		<!-- {#if step === 1} -->
		<Title ordering={1}>Wizard</Title>

		<Text>
			To find the optimal amount of how many calories you should consume per day to reach a specific
			goal, it's a good idea to calculate your TDEE.
		</Text>

		<Text>
			In order to do so, please provide some necessary information about yourself to make the
			calculation work.
		</Text>

		<Title ordering={2}>Step 1</Title>

		<NumberInput name="age" label="Age" bind:value={tdee.age} />

		<NativeSelect
			name="sex"
			label="Sex"
			data={sex}
			bind:value={tdee.sex}
			placeholder="Pick one"
		/>

		<NumberInput
			name="height"
			label="Height"
			bind:value={tdee.height}
			description="Your height in cm."
		/>

		<NumberInput
			name="weight"
			label="Weight"
			bind:value={tdee.weight}
			description="Your current weight in kg."
		/>
		<!--{/if} -->

		<Title ordering={2}>Step 2</Title>

		<Text>
			How active are you during your day? Please choose what describes your daily activity level
			best.
		</Text>

		<RadioGroup
			name="activityLevel"
			items={activityLevels}
			bind:value={tdee.activityLevel}
			itemDirection={'down'}
		/>

		<Title ordering={2}>Step 3</Title>

		<Text>Do you aim for weight loss or weight gain?</Text>

		<RadioGroup name="gain" items={goals} bind:value={tdee.calculationGoal} />

		<Text>How much weight are you looking to lose/gain per week?</Text>

		<div class="slider-container">
			<input name="diff" class="slider" type="range" min="0" max="7" bind:value={tdee.weeklyDifference} />
		</div>
		<Text>{tdee.weeklyDifference / 10}kg</Text>

		<Button>Confirm</Button>
	</form>


	{#if form?.status === 200}
		<Title ordering={2}>Your result</Title>

		<Text>
			Based on your input, your basic metabolic rate is {form.tdee.bmr}kcal. Your daily calorie
			consumption to hold your weight should be around {form.tdee.tdee}kcal.
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
		-webkit-transition: 0.2s;
		transition: opacity 0.2s;
	}

	.slider:hover {
		opacity: 1;
	}

	.slider::-webkit-slider-thumb {
		-webkit-appearance: none;
		appearance: none;
		width: 25px;
		height: 25px;
		background: #04aa6d;
		cursor: pointer;
	}

	.slider::-moz-range-thumb {
		width: 25px;
		height: 25px;
		background: #04aa6d;
		cursor: pointer;
	}
</style>
