<script>
	import {createEventDispatcher} from "svelte";

	export let value, dateStr;
	export let disabled = false;
	export let category = 'b';

	const dispatch = createEventDispatcher();

	const add = () => {
		dispatch('add', {
			value, category, dateStr
		});
	};

	const edit = () => {};
	const remove = () => {};

	const categories = [
		{ label: 'Breakfast', value: 'b' },
		{ label: 'Lunch', value: 'l' },
		{ label: 'Dinner', value: 'd' },
		{ label: 'Snack', value: 's' }
	];
</script>

<div class="flex gap-2">
	<div class="input-group input-group-divider grid-cols-[auto_1fr_auto]">
		<div class="input-group-shim">kcal</div>
		<input type="number" placeholder="Amount..." bind:value {disabled} />
		<select {disabled} bind:value={category}>
			{#each categories as category}
				<option value={category.value}>{category.label}</option>
			{/each}
		</select>
	</div>
	{#if disabled}
		<button class="btn variant-filled-primary" disabled={!disabled} on:click|preventDefault={edit}>Edit</button>
	{:else}
		<button class="btn variant-filled-primary" {disabled} on:click|preventDefault={add}>Add</button>
	{/if}
</div>
