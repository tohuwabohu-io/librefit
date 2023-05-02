<script type="ts">
	import { Configuration, CalorieTrackerResourceApi } from 'librefit-api/rest';
	import { onMount } from 'svelte';
	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
	import { Accordion } from '@skeletonlabs/skeleton';
	import {getDateAsStr} from '$lib/util';

	const api = new CalorieTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	let today = new Date();
	let todayStr = getDateAsStr(today)

	let availableDates = new Set<String>();
	availableDates.add(todayStr);

	onMount(async () => {
		await api
			.trackerCaloriesListUserIdDatesGet({
				userId: 1
			})
			.then((dates: Array<String>) => {
				dates.forEach((d) => availableDates.add(d));
				availableDates = availableDates;
			})
			.catch((e) => console.error(e));
	});
</script>

<svelte:head>
	<title>LibreFit - Calorie Tracker</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<Accordion class="variant-ghost-surface rounded-xl">
			{#each [...availableDates] as date}
				<CalorieTracker bind:date today={todayStr} />
			{/each}
		</Accordion>
	</div>
</section>
