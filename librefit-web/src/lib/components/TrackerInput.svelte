<script lang="ts">
	import {createEventDispatcher} from 'svelte';

	export let value, dateStr, id;
	export let existing = false;
	export let disabled = false;
	export let categories;
	export let category;

	const dispatch = createEventDispatcher();

	let editing = false;

	let previous: {category, value};
	let changeAction;

	const add = () => {
		dispatch('add', {
			value,
			category,
			dateStr
		});
	};

	const change = (action: String) => {
		return () => {
			disabled = false;
			editing = true;

			changeAction = action;

			if (action === 'update') {
				previous = { category, value }
			}
		}
	}

	const update = (e: Event) => {
		e.preventDefault();

		if (value != previous.value || category != previous.category) {
			dispatch('update', {
				id, value, category
			});
		}

		disabled = true;
		editing = false;
	};

	const remove = (e: Event) => {
		e.preventDefault();

		dispatch('remove', {
			id
		});

		disabled = true;
		editing = false;
	};

	const discard = () => {
		disabled = true;
		editing = false;

		value = previous.value;
		category = previous.category;
	}
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
	{#if existing}
		{#if !editing}
			<button
				class="btn-icon variant-filled-secondary"
				on:click|preventDefault={change('update')}>
				E
			</button>
			<button
				class="btn-icon variant-filled"
				on:click|preventDefault={change('delete')}>
				D</button>
		{:else}
			<button class="btn-icon variant-ghost-primary"
					on:click={changeAction === 'update' ? update : remove}>
				Y
			</button>
			<button class="btn-icon variant-ghost-error"
					on:click|preventDefault={discard}>
				N
			</button>
		{/if}
	{:else}
		<button class="btn-icon variant-filled-primary"
				on:click|preventDefault={add}>
			A
		</button>
	{/if}
</div>
