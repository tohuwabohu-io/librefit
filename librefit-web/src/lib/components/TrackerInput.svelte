<script>
	export let value;
	export let disabled = false;
	export let category = 'b';

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
		<button class="btn variant-filled-primary" disabled={!disabled}>Edit</button>
	{:else}
		<button class="btn variant-filled-primary" {disabled}>Add</button>
	{/if}
</div>
