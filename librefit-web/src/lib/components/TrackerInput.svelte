<script>
	import { createEventDispatcher } from 'svelte';
	import Trash from '$lib/assets/icons/trash.svg?component';
	import AddKcal from '$lib/assets/icons/hamburger-plus.svg?component';
	import AddWeight from '$lib/assets/icons/plus.svg?component';
	import Add from '$lib/assets/icons/plus.svg?component';
	import Edit from '$lib/assets/icons/pencil.svg?component';
	import Check from '$lib/assets/icons/check.svg?component';
	import CancelDelete from '$lib/assets/icons/trash-off.svg?component';
	import CancelEdit from '$lib/assets/icons/pencil-off.svg?component';
	import {getDaytimeFoodCategory} from '$lib/date.js';

	export let value, dateStr, sequence;
	export let existing = false;
	export let disabled = false;
	export let quickadd = false;

	/** @type Array<FoodCategory> */
	export let categories;

	/** @type FoodCategory */
	export let category = categories ? categories.filter(c => c.shortvalue === getDaytimeFoodCategory(new Date()))[0] : undefined;

	export let unit;

	const dispatch = createEventDispatcher();

	let editing = false;

	let previous;
	let changeAction;

	let btnAdd, btnConfirm, btnCancel;

	const add = () => {
		btnAdd.disabled = true;

		dispatch('add', {
			sequence: sequence,
			dateStr: dateStr,
			value: value,
			category: category,
			callback: () => { btnAdd.disabled = false }
		});
	};

	const change = (action) => {
		return () => {
			disabled = false;
			editing = true;

			changeAction = action;

			if (action === 'update') {
				previous = { category, value };
			}
		};
	};

	const update = (e) => {
		e.preventDefault();

		btnConfirm.disabled = true;
		btnCancel.disabled = true;

		if (value !== previous.value || category !== previous.category) {
			dispatch('update', {
				sequence: sequence,
				dateStr: dateStr,
				value: value,
				category: category,
				callback: postAction
			});
		} else {
			postAction();
		}
	};

	const remove = (e) => {
		e.preventDefault();

		btnConfirm.disabled = true;
		btnCancel.disabled = true;

		dispatch('remove', {
			sequence: sequence,
			dateStr: dateStr,
			target: btnConfirm,
			callback: postAction
		});
	};

	const discard = () => {
		disabled = true;
		editing = false;

		if (changeAction === 'update') {
			value = previous.value;
			category = previous.category;
		}
	};

	const postAction = (error) => {
		if (editing) {
			btnConfirm.disabled = false;
			btnCancel.disabled = false;
		}

		if (error) discard();
		else {
			disabled = true;
			editing = false;
		}
	}
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
			<input class="max-sm:max-w-36 unset-fit" type="number" placeholder="Amount..." aria-label="amount" bind:value {disabled} />
			{#if categories}
			<select aria-label="category" {disabled} bind:value={category}>
				{#each categories as category}
					<option value={category.shortvalue}>{category.longvalue}</option>
				{/each}
			</select>
			{/if}
		</div>
		{#if quickadd}
		<div>
			<button aria-label="add" bind:this={btnAdd} class="btn-icon variant-filled-primary" on:click|preventDefault={add}>
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
		</div>
		{/if}
	</div>
	{#if !quickadd}
	<div class="flex flex-row gap-1 justify-end">
		<div>
			{#if !existing}
			<div>
				<button aria-label="add" bind:this={btnAdd} class="btn-icon variant-filled-primary" on:click|preventDefault={add}>
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
			</div>
			{:else}
			{#if !editing}
			<button aria-label="edit" class="btn-icon variant-filled-secondary" on:click|preventDefault={change('update')}>
				<span>
					<Edit/>
				</span>
			</button>
			<button aria-label="delete" class="btn-icon variant-filled" on:click|preventDefault={change('delete')}>
				<span>
					<Trash/>
				</span>
			</button>
			{:else}
			<button aria-label="confirm" bind:this={btnConfirm}
				class="btn-icon variant-ghost-primary"
				on:click={changeAction === 'update' ? update : remove}
			>
				<span>
					<Check/>
				</span>
			</button>
			<button aria-label="discard" bind:this={btnCancel} class="btn-icon variant-ghost-error" on:click|preventDefault={discard}>
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
		{/if}
		</div>
	</div>
	{/if}
</div>
