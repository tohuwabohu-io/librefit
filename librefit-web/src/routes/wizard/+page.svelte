<script lang="ts">
	import { CalculationGoal, CalculationSex, Tdee } from 'librefit-api/rest';
	import {RadioGroup, RadioItem} from "@skeletonlabs/skeleton";

	/** @type {import('./$types').ActionData} */ export let form;

	let step = 1;
	let tdee: Tdee = {
		age: 0,
		height: 0,
		sex: CalculationSex.Female,
		weight: 0,
		activityLevel: 1,
		calculationGoal: CalculationGoal.Loss,
		target: 0,
		weeklyDifference: 0
	};

	if (form) {
		tdee = form;
	}

	const activityLevels = [
		{ label: 'Level 1: Mostly Sedentary', value: 1 },
		{ label: 'Level 2: Light Activity', value: 1.25 },
		{ label: 'Level 3: Moderate Activity', value: 1.5 },
		{ label: 'Level 4: Highly Active', value: 1.75 },
		{ label: 'Level 5: Professional Athlete', value: 2 }
	];

	const sex = [CalculationSex.Female, CalculationSex.Male];

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

<div>
	<form method="POST">
		<!-- {#if step === 1} -->
		<h1>Wizard</h1>

		<p>
			To find the optimal amount of how many calories you should consume per day to reach a specific
			goal, it's a good idea to calculate your TDEE.
		</p>

		<p>
			In order to do so, please provide some necessary information about yourself to make the
			calculation work.
		</p>

		<h2>Step 1</h2>

		<label>
			<span>
				Age
			</span>
			<input class="input" type="number" name="age" bind:value={tdee.age} />
		</label>

		<label>
			<span>
				Sex
			</span>

			<select class="select" name="sex" bind:value={tdee.sex}>
				<option value="" disabled selected>Pick one</option>
				{#each sex as s}
					<option value={s}>
						s
					</option>
				{/each}
			</select>
		</label>

		<label>
			<span>Height</span>
			<input type="number" bind:value={tdee.height}>
		</label>
			<!-- description="Your height in cm." -->

		<label>
			<span>Weight</span>
			<input type="number" bind:value={tdee.weight}>
		</label>

			<!--description="Your current weight in kg."-->
		<!--{/if} -->

		<h2>Step 2</h2>

		<p>
			How active are you during your day? Please choose what describes your daily activity level
			best.
		</p>

		<label>
			<span>Activity Level</span>

			<RadioGroup>
				{#each activityLevels as activityLevel}
					<RadioItem bind:group={tdee.activityLevel} name="activityLevel" value={activityLevel.value}>{activityLevel.label}</RadioItem>
				{/each}
			</RadioGroup>
		</label>

		<h2>Step 3</h2>

		<p>Do you aim for weight loss or weight gain?</p>

		<label>
			<span>Goal</span>

			<RadioGroup>
				{#each goals as goal}
					<RadioItem bind:group={tdee.calculationGoal} name="gain" value={goal.value}>{goal.label}</RadioItem>
				{/each}
			</RadioGroup>
		</label>

		<p>How much weight are you looking to lose/gain per week?</p>

		<label class="label">
			<span>Gain/Loss</span>
			<input type="range" bind:value={tdee.weeklyDifference} min="0" max="7" />
		</label>

		<p>{tdee.weeklyDifference / 10}kg</p>

		<button>Confirm</button>
	</form>

	{#if form}
		<h2>Your result</h2>

		<p>
			Based on your input, your basic metabolic rate is {tdee.bmr}kcal. Your daily calorie
			consumption to hold your weight should be around {tdee.tdee}kcal.
		</p>
	{/if}

	<button on:click={previousStep}>Previous</button>
	<button on:click={nextStep}>Next</button>
</div>
