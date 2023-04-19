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
	import { convertDateStrToDisplayDateStr } from '$lib/util.js';

	export let date: String;
	export let today: String;

	let total = 0;
	let sequence = 1;
	let addValue = 0;
	let trackerEntries = new Array<CalorieTrackerEntry>();

	$: if (trackerEntries.length > 0) {
		total = trackerEntries.map((entry) => entry.amount).reduce((a, b) => a + b);
		sequence = Math.max(...trackerEntries.map((entry) => entry.id)) + 1;
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
			id: e.detail.sequence,
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

				addValue = 0;
			})
			.catch(console.error);
	};

	const updateEntry = (e) => {
		api
			.trackerCaloriesReadUserIdDateIdGet({
				userId: 1,
				id: e.detail.sequence,
				date: e.detail.date
			})
			.then((entry: CalorieTrackerEntry) => {
				entry.amount = e.detail.value;
				entry.category = e.detail.category;

				api
					.trackerCaloriesUpdatePut({ calorieTrackerEntry: entry })
					.then((_) => console.log('updated!'))
					.catch(console.error);
			});
	};

	const deleteEntry = (e) => {
		api
			.trackerCaloriesDeleteUserIdDateIdDelete({
				userId: 1,
				id: e.detail.sequence,
				date: e.detail.date
			})
			.then((_) => {
				trackerEntries = trackerEntries.filter((entry) => entry.id !== e.detail.sequence);
			})
			.catch(console.error);
	};

	const initialLoad = async () => {
		if (date !== today) {
			await loadEntry();
		}
	};

	const loadEntry = async () => {
		await api
			.trackerCaloriesListUserIdDateGet({
				date: date,
				userId: 1
			})
			.then((entries: Array<CalorieTrackerEntry>) => {
				if (entries.length > 0) {
					total = entries.map((entry) => entry.amount).reduce((a, b) => a + b);

					trackerEntries.push(...entries);
					trackerEntries = trackerEntries;
				}
			})
			.catch(console.error);
	};

	onMount(async () => {
		if (date === today) {
			await loadEntry();
		}
	});
</script>

<AccordionItem id={date} open={date === today} on:toggle|once={initialLoad}>
	<svelte:fragment slot="summary">{convertDateStrToDisplayDateStr(date)}</svelte:fragment>
	<svelte:fragment slot="content">
		<div class="flex gap-4 justify-between">
			<TrackerRadial bind:current={total} />

			<div class="flex flex-col grow gap-4">
				<TrackerInput
					categories={categories}
					bind:value={addValue}
					dateStr={date}
					bind:id={sequence}
					on:add={addEntry}
					unit={'kcal'}
				/>
				{#each trackerEntries as trackerInput}
					<TrackerInput
						disabled={true}
						existing={true}
						value={trackerInput.amount}
						categories={categories}
						category={trackerInput.category}
						dateStr={date}
						id={trackerInput.id}
						on:add={addEntry}
						on:update={updateEntry}
						on:remove={deleteEntry}
						unit={'kcal'}
					/>
				{/each}
			</div>
		</div>
	</svelte:fragment>
</AccordionItem>
