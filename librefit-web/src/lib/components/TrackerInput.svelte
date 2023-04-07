<script lang="ts">
	import { createEventDispatcher } from 'svelte';
	import Trash from '$lib/assets/icons/trash.svg?component';
	import AddKcal from '$lib/assets/icons/hamburger-plus.svg?component';
	import AddWeight from '$lib/assets/icons/plus.svg?component';
	import Add from '$lib/assets/icons/plus.svg?component';
	import Edit from '$lib/assets/icons/pencil.svg?component';
	import Check from '$lib/assets/icons/check.svg?component';
	import CancelDelete from '$lib/assets/icons/trash-off.svg?component';
	import CancelEdit from '$lib/assets/icons/pencil-off.svg?component';

	export let value, dateStr, id;
	export let existing = false;
	export let disabled = false;
	export let categories;
	export let category;
	export let unit;

	const dispatch = createEventDispatcher();

	let editing = false;

	let previous: { category; value };
	let changeAction;

	const add = () => {
		dispatch('add', {
			value,
			id,
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
				previous = { category, value };
			}
		};
	};

	const update = (e: Event) => {
		e.preventDefault();

		if (value != previous.value || category != previous.category) {
			dispatch('update', {
				id,
				dateStr,
				value,
				category
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

		if (changeAction === 'update') {
			value = previous.value;
			category = previous.category;
		}
	};
</script>

<div class="flex gap-2">
	<div class="input-group input-group-divider grid-cols-[auto_1fr_auto]">
		<div class="input-group-shim">{unit}</div>
		<input type="number" placeholder="Amount..." bind:value {disabled} />
		{#if categories}
			<select {disabled} bind:value={category}>
				{#each categories as category}
					<option value={category.value}>{category.label}</option>
				{/each}
			</select>
		{/if}
	</div>
	{#if existing}
		{#if !editing}
			<button class="btn-icon variant-filled-secondary" on:click|preventDefault={change('update')}>
				<span>
					<Edit/>
				</span>
			</button>
			<button class="btn-icon variant-filled" on:click|preventDefault={change('delete')}>
				<span>
					<Trash/>
				</span>
			</button>
		{:else}
			<button
				class="btn-icon variant-ghost-primary"
				on:click={changeAction === 'update' ? update : remove}
			>
				<span>
					<Check/>
				</span>
			</button>
			<button class="btn-icon variant-ghost-error" on:click|preventDefault={discard}>
				<span>
					{#if changeAction === 'update'}
						<CancelEdit/>
					{:else if changeAction === 'delete'}
						<CancelDelete/>
					{:else}
						X
					{/if}
				</span>
			</button>
		{/if}
	{:else}
		<button class="btn-icon variant-filled-primary" on:click|preventDefault={add}>
			<span>
				{#if unit === 'kcal'}
					<AddKcal/>
				{:else if unit === 'kg'}
					<AddWeight/>
				{:else}
					<Add/>
				{/if}
			</span>
		</button>
	{/if}
</div>
