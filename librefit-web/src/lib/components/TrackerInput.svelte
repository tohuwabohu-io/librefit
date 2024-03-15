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
	import {Category} from '$lib/api/model.js';

	export let value, dateStr, sequence;
	export let existing = false;
	export let disabled = false;
	export let categories = null;
	export let category = Category.Unset;
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
			date: dateStr,
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
				date: dateStr,
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
			date: dateStr,
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

<div class="flex flex-row gap-2">
	<div class="input-group max-2xl:md:input-group-divider grid-cols-[auto_1fr_auto]">
		<div class="input-group-shim max-sm:!hidden">{unit}</div>
		<input class="input" type="number" placeholder="Amount..." bind:value {disabled} />
		{#if categories}
			<select {disabled} bind:value={category}>
				{#each categories as category}
					<option value={category.value}>{category.label}</option>
				{/each}
			</select>
		{/if}
	</div>
	<div class="flex flex-row gap-1">
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
			<button bind:this={btnConfirm}
				class="btn-icon variant-ghost-primary"
				on:click={changeAction === 'update' ? update : remove}
			>
				<span>
					<Check/>
				</span>
			</button>
			<button bind:this={btnCancel} class="btn-icon variant-ghost-error" on:click|preventDefault={discard}>
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
		<button bind:this={btnAdd} class="btn-icon variant-filled-primary" on:click|preventDefault={add}>
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
</div>
