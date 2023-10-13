<script>
	import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';
	import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/util';
	import {handleApiError, showToastSuccess} from '$lib/toast.js';
	import TrackerRadial from '$lib/components/TrackerRadial.svelte';
	import TrackerInput from '$lib/components/TrackerInput.svelte';
	import {Category} from '$lib/api/index.js';

	let today = new Date();
	let todayStr = getDateAsStr(today);

	export let data;

	let datesToEntries = {};

	$: datesToEntries;

	const categories = Object.keys(Category).map((key) => {
		return {
			label: key,
			value: Category[key]
		};
	});

	const addEntry = (e) => {
		console.log(e);

		/** @type {CalorieTrackerEntry} */
		const newEntry = {
			id: e.detail.sequence,
			added: today,
			amount: e.detail.value,
			category: e.detail.category
		};

		fetch('/tracker', {
			method: 'POST',
			body: JSON.stringify(newEntry)
		}).then(async (response) => {
			/*trackerEntries.push(await response.json());
			trackerEntries = trackerEntries;

			addValue = 0;*/
		}).catch(handleApiError);
	};

	const updateEntry = (e) => {
		/** @type {CalorieTrackerEntry} */
		const entry = {
			id: e.detail.sequence,
			added: e.detail.date,
			amount: e.detail.value,
			category: e.detail.category
		};

		fetch('/tracker', {
			method: 'PUT',
			body: JSON.stringify(entry)
		}).then(_ => showToastSuccess('Entry updated successfully!')).catch(handleApiError)
	};

	const deleteEntry = (e) => {
		fetch(`/tracker?id=${e.detail.sequence}&added=${e.detail.date}`, {
			method: 'DELETE',
		}).then((response) => {
			if (response.status === 200) {
				// loadEntries(e.detail.date);
			} else {
				throw Error(response.status);
			}
		}).catch(handleApiError)
	};

	const loadEntries = async (added) => {
		const response = await fetch(`/tracker?added=${added}`, {method: 'GET'});
		const result = response.json();

		datesToEntries[added] = await result;

		if (response.ok) {
			return result;
		} else {
			throw new Error(result);
		}
	}

	/** @param {Array<CalorieTrackerEntry>} entries */
	const calculateTotal = async (entries) => {
		return entries.map((entry) => entry.amount).reduce((a, b) => a + b);
	}
</script>

<svelte:head>
	<title>LibreFit - Calorie Tracker</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		{#if data.availableDates}
			{#each [...data.availableDates] as dateStr}
			<Accordion class="variant-ghost-surface rounded-xl">
				<AccordionItem id={dateStr} open={dateStr === todayStr} on:toggle|once={loadEntries(dateStr)}>
					<svelte:fragment slot="summary">
						{convertDateStrToDisplayDateStr(dateStr)}
					</svelte:fragment>
					<svelte:fragment slot="content">
						<div class="flex gap-4 justify-between">
							{#await datesToEntries[dateStr]}
								<p>... loading</p>
							{:then entries}
								{#if entries}
								<TrackerRadial entries={entries.map(e => e.amount)} />
								<div class="flex flex-col grow gap-4">
									{#each entries as entry}
										<TrackerInput
												categories={categories}
												value={entry.amount}
												dateStr={dateStr}
												id={entry.id}
												on:add={addEntry}
												on:update={updateEntry}
												on:remove={deleteEntry}
												existing={entry.id !== undefined}
												unit={'kcal'}
										/>
									{/each}
								</div>
								{/if}
							{:catch error}
								<p>{error}</p>
							{/await}
						</div>
					</svelte:fragment>
				</AccordionItem>
			</Accordion>
			{/each}
		{:else}
			{data.error}
		{/if}
	</div>
</section>
