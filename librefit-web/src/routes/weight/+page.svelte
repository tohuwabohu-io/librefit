<script lang="ts">
	import { RadioGroup, RadioItem } from '@skeletonlabs/skeleton';
	import { Configuration, WeightTrackerResourceApi } from 'librefit-api/rest';
	import { DataViews, enumKeys } from '$lib/util';
	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import { onMount } from 'svelte';

	let filter = DataViews.Today;

	const api = new WeightTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	onMount(async () => {});
</script>

<svelte:head>
	<title>LibreFit - Weight Tracker</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<RadioGroup>
			{#each enumKeys(DataViews) as dataView}
				<RadioItem bind:group={filter} name="justify" value={DataViews[dataView]}
					>{dataView}</RadioItem
				>
			{/each}
		</RadioGroup>
	</div>
</section>
