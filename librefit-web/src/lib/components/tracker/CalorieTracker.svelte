<script lang="ts">
	import {
		CalorieTrackerEntry,
		CalorieTrackerResourceApi,
		Category,
		Configuration
	} from 'librefit-api/rest';
	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import { onMount } from 'svelte';
	import TrackerInput from '$lib/components/TrackerInput.svelte';
	import { AccordionItem } from '@skeletonlabs/skeleton';
	import TrackerRadial from '$lib/components/TrackerRadial.svelte';

	export let date: String;
	export let today: String;

	let currentValue = 0;
	let trackerEntries = new Array<CalorieTrackerEntry>();

	$: if (trackerEntries.length > 0) {
		currentValue = trackerEntries.map((entry) => entry.amount).reduce((a, b) => a + b);
	}

	const categories = Object.keys(Category).map((key) => {
		return {
			label: key,
			value: Category[key]
		};
	});

	const api = new CalorieTrackerResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const addEntry = (e) => {
		const newEntry: CalorieTrackerEntry = {
			userId: 1,
			added: today,
			amount: e.detail.value,
			category: e.detail.category
		};

		api
			.trackerCaloriesCreatePost({
				calorieTrackerEntry: newEntry
			})
			.then((result: CalorieTrackerEntry) => {
				trackerEntries.push(result);
				trackerEntries = trackerEntries;
			})
			.catch(console.error);
	};

	const editEntry = (e) => {};

	const removeEntry = (e) => {
		api
			.trackerCaloriesDeleteIdDelete({ id: e.detail.id })
			.then((_) => {
				trackerEntries = trackerEntries.filter((entry) => entry.id !== e.detail.id);
			})
			.catch(console.error);
	};

	const loadEntry = async () => {
		await api
			.trackerCaloriesListUserIdDateGet({
				date,
				userId: 1
			})
			.then((entries: Array<CalorieTrackerEntry>) => {
				if (entries.length > 0) {
					currentValue = entries.map((entry) => entry.amount).reduce((a, b) => a + b);

					trackerEntries.push(...entries);
					trackerEntries = trackerEntries;
				}
			})
			.catch(console.error);
	};

	onMount(async () => {
		if (date == today) {
			await loadEntry();
		}
	});
</script>

<AccordionItem id={date} open={date === today} on:toggle|once={loadEntry}>
	<svelte:fragment slot="summary">{date}</svelte:fragment>
	<svelte:fragment slot="content">
		<div class="flex gap-4 justify-between">
			<TrackerRadial bind:current={currentValue} />

			<div class="flex flex-col grow gap-4">
				<TrackerInput {categories} value="" dateStr={date} id={-1} on:add={addEntry} />
				{#each trackerEntries as trackerInput}
					<TrackerInput
						disabled={true}
						value={trackerInput.amount}
						{categories}
						category={trackerInput.category}
						dateStr={date}
						id={trackerInput.id}
						on:add={addEntry}
						on:edit={editEntry}
						on:remove={removeEntry}
					/>
				{/each}
			</div>
		</div>
	</svelte:fragment>
</AccordionItem>
