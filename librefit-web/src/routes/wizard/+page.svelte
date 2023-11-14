<script>
	import TdeeStepper from '$lib/components/TdeeStepper.svelte';s

	/** @type Tdee */
	let calculationResult;

	let calculationError;

	async function calculate(e) {
		await fetch('/wizard', {
			method: 'POST',
			body: JSON.stringify(e.detail.tdee),
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(async (response) => {
			if (response.ok) {
				calculationResult = await response.json()
			} else {
				calculationError = true;
			}
		}).catch(console.error);
	}
</script>

<svelte:head>
	<title>LibreFit - TDEE Wizard</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-8">
		<h1>TDEE Calculator</h1>

		{#if !calculationResult && !calculationError}
			<TdeeStepper on:calculate={calculate}/>
		{:else if !calculationError}
			<h2>Your result</h2>

			<p>
				Based on your input, your basic metabolic rate is {calculationResult.bmr}kcal. Your daily calorie
				consumption to hold your weight should be around {calculationResult.tdee}kcal.
			</p>
			<p>
				Deficit: {calculationResult.deficit}
			</p>
			<p>
				Target: {calculationResult.target}
			</p>
		{:else}
			<p>
				An error occured. Please try again later.
			</p>
		{/if}
	</div>
</section>

<style>
</style>
