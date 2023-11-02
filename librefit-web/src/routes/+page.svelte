<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {categoriesAsKeyValue, getDaytimeGreeting} from '$lib/util.js';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';

	export let data;
</script>

<section>
	{#if data.authenticated === true}
	<div class="container mx-auto p-8 space-y-8">

		<h1>Good {getDaytimeGreeting(new Date())}, {data.user.name}!</h1>
		<h2>This is your daily summary.</h2>

		<div class="flex flex-row gap-8">
			<div class="flex flex-row gap-4 variant-ghost-surface rounded-xl p-4">
				<CalorieTracker entries={data.lastCt} categories={categoriesAsKeyValue} />
			</div>
			<div class="variant-ghost-surface rounded-xl p-4">
				<WeightTracker entries={[]} lastEntry={data.lastWeight} goal={data.lastGoal}/>
			</div>
		</div>
	</div>
	{:else}
	<div class="container mx-auto p-8 space-y-8 text-center">
		<h1 class="font-logo">
			<span class="text-primary-500 text-9xl">Libre</span>
			<span class="text-secondary-500 text-9xl">Fit</span>
		</h1>

		<p class="unstyled text-3xl">Welcome to LibreFit, an Open Source calorie tracker!</p>
	</div>
	{/if}
</section>
