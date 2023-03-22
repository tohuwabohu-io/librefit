<script lang="ts">
	import { createEventDispatcher } from 'svelte';

	import Icon from '@iconify/svelte';

	export let value, dateStr, id;
	export let disabled = false;
	export let categories;
	export let category;

	const dispatch = createEventDispatcher();

	const add = () => {
		dispatch('add', {
			value,
			category,
			dateStr
		});
	};

	const edit = () => {
		dispatch('edit', {
			dateStr,
			id
		});
	};

	const remove = () => {
		dispatch('remove', {
			dateStr,
			id
		});
	};
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
		<button
			class="btn rounded-full variant-filled-secondary"
			disabled={!disabled}
			on:click|preventDefault={edit}><Icon icon="iconoir:edit-pencil" /></button
		>
		<button
			class="btn btn-icon variant-filled"
			disabled={!disabled}
			on:click|preventDefault={remove}>D</button
		>
	{:else}
		<button class="btn btn-icon variant-filled-primary" {disabled} on:click|preventDefault={add}
			>A</button
		>
	{/if}
</div>
