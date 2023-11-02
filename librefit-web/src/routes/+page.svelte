<script>
	import WeightTracker from '$lib/components/tracker/WeightTracker.svelte';
	import {getDaytimeGreeting} from '$lib/util.js';
	import {page} from '$app/stores';

	export let data;
</script>

<section>
	{#if data.authenticated === true}
	<h1>Good {getDaytimeGreeting(new Date())}!</h1>
	<div class="container mx-auto p-8 space-y-8 text-center">

		<WeightTracker entries={[]} lastEntry={data.lastWeight} goal={data.lastGoal}/>
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
