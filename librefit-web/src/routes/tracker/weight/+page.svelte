<script>
	import {getToastStore, RadioGroup, RadioItem} from '@skeletonlabs/skeleton';
	import {paintWeightTrackerEntries} from '$lib/chart.js';
	import {Line} from 'svelte-chartjs';
	import {Chart, registerables} from 'chart.js';
	import {showToastError} from '$lib/toast.js';
	import {getContext} from 'svelte';
	import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
	import {listWeightFiltered} from '$lib/api/tracker.js';
	import {DataViews, enumKeys} from '$lib/enum.js';
	import {goto} from '$app/navigation';

	Chart.register(...registerables);

	const toastStore = getToastStore();
	const indicator = getContext('indicator');
	const user = getContext('user');

	if (!$user) goto('/');

	export let filter = DataViews.Month;

	/** @type import('./$types').PageData */
	export let data;

	const today = new Date();

	let entries;
	let chartData, chartOptions;

	const currentGoal = getContext('currentGoal');
	const lastEntry = getContext('lastWeight')

	const loadEntriesFiltered = async () => {
		$indicator = $indicator.start();

		await listWeightFiltered(filter).then(async (result) => {
			/** @type Array<WeightTrackerEntry> */
			entries = await result.json();

			paint(entries);
		}).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
	}

	const paint = (entries) => {
		const paintMeta = paintWeightTrackerEntries(entries, today, filter);

		chartData = paintMeta.chartData;
		chartOptions = paintMeta.chartOptions;
	}

	$: if (data && data.entries) {
		entries = data.entries;

		paint(data.entries);
	}

	// TODO unused
	const add = (e) => {
	}

	// TODO unused
	const update = (e) => {
	}

	// TODO unused
	const remove = (e) => {
	}
</script>

<svelte:head>
	<title>LibreFit - Weight Tracker</title>
</svelte:head>

{#if $user}
<section>
	<div class="container mx-auto p-8 space-y-10">
		<div class="flex flex-col gap-4">
			<RadioGroup>
				{#each enumKeys(DataViews) as dataView}
					<RadioItem bind:group={filter}
							   name="justify"
							   value={DataViews[dataView]}
							   on:change={loadEntriesFiltered}>
						{dataView}
					</RadioItem>
				{/each}
			</RadioGroup>

			{#if chartData }
				<Line data={chartData} options={chartOptions} />
			{:else}
				<div class="flex flex-col items-center text-center gap-4">
					<NoScale width={100} height={100}/>
					<p>
						Insufficient data to render your history.
					</p>
				</div>
			{/if}
		</div>
	</div>
</section>
{/if}