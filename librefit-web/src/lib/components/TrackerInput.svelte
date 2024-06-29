<script>
	import {createEventDispatcher} from 'svelte';
	import {getDaytimeFoodCategory} from '$lib/date.js';
	import TrackerButtons from './TrackerButtons.svelte';

	export let value, dateStr, sequence;
	export let existing = false;
	export let disabled = false;
	export let compact = false;

	/** @type Array<FoodCategory> */
	export let categories;

	/** @type FoodCategory */
	export let category = categories ? categories.filter(c => c.shortvalue === getDaytimeFoodCategory(new Date()))[0] : undefined;

	export let unit;

	export let maxWidthCss = 'sm:max-w-36';
	export let placeholder = 'Amount...';

	const dispatch = createEventDispatcher();

	let previous;
	let changeAction;

	const add = (e) => {
		dispatch('add', {
			sequence: sequence,
			dateStr: dateStr,
			value: value,
			category: category,
			callback: () => { e.detail.callback() }
		});
	};

	const update = (e) => {
		e.preventDefault();

		if (value !== previous.value || category !== previous.category) {
			dispatch('update', {
				sequence: sequence,
				dateStr: dateStr,
				value: value,
				category: category,
				callback: () => e.detail.callback()
			});
		} else {
			postAction();
		}
	};

	const remove = (e) => {
		e.preventDefault();

		dispatch('remove', {
			sequence: sequence,
			dateStr: dateStr,
			target: e.target,
			callback: () => e.detail.callback()
		});
	};
</script>

<style>
	.unset-fit {
		min-width: unset !important;
	}
</style>

<div class="flex flex-col gap-2">
	<div class="flex flex-row gap-2">
		<div class="input-group input-group-divider grid-cols-[auto_1fr_auto]">
			<div class="input-group-shim">{unit}</div>
			<input class="{maxWidthCss} unset-fit" type="number" placeholder={placeholder} aria-label="amount" bind:value {disabled} />
			{#if categories}
			<select aria-label="category" {disabled} bind:value={category}>
				{#each categories as category}
					<option value={category.shortvalue}>{category.longvalue}</option>
				{/each}
			</select>
			{/if}
		</div>
		{#if compact}
		<TrackerButtons {unit} {existing}
				on:add={add}
				on:update={update}
				on:remove={remove}
				bind:previous={previous}
				bind:changeAction={changeAction}
				bind:disabled={disabled}
				bind:value
				bind:category
		/>
		{/if}
	</div>
	{#if !compact}
		<TrackerButtons {unit} {existing}
						on:add={add}
						on:update={update}
						on:remove={remove}
						bind:previous={previous}
						bind:changeAction={changeAction}
						bind:disabled={disabled}
						bind:value
						bind:category
		/>
	{/if}
</div>
