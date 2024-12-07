<svelte:options accessors={true} />

<script lang="ts">
	import type { ValidationMessage } from '$lib/model';

	export let value: any = '';
	export let name = 'control';
	export let label = '';
	export let type = 'text';
	export let styling = 'input';
	export let placeholder = '';
	export let unit = '';

	export let validateDetail = (e): ValidationMessage => {
		return {
			valid: false,
			skip: true
		};
	};

	export let emptyMessage = `${label ? label : 'Field'} is empty.`;
	export let required = false;
	export let errorMessage = undefined;

	export let readOnly = false;

	const getType = (node) => {
		node.type = type;
	};

	export let validate = () => {

		let valid = false;
		let detail = validateDetail({
			value: value,
			label: label
		});
		if (required && isEmpty()) {

			errorMessage = emptyMessage;
		} else if (!detail.skip) {
			valid = detail.valid;
			errorMessage = detail.errorMessage;
		} else {
			errorMessage = undefined;
			valid = true;
		}
		return valid;

	};
	const isEmpty = () => {

		return value === undefined || value === null || value.length <= 0;
	};

	$: value;
	$: label;
	$: type;
	$: errorMessage;
	$: validateDetail;
</script>

<label class="label">
	{#if type !== 'checkbox'}
		<span class="flex justify-between">
			{#if label}
				<span class="self-center">
					{label}
				</span>
			{/if}

			{#if errorMessage}
				<span class="text-sm validation-error-text self-center">
					{errorMessage}
				</span>
			{:else}
				<span class="text-sm self-center"></span>
			{/if}
		</span>
		<div class={!unit ? '' : 'input-group input-group-divider grid-cols-[auto_1fr_auto]'}>
			{#if unit}
				<div class={'input-group-shim' + (!errorMessage ? '' : ' input-error')}>{unit}</div>
			{/if}
			<input
				{name}
				class={styling + (!unit ? '' : 'rounded-none') + (!errorMessage ? '' : ' input-error' )}
				use:getType
				{placeholder}
				{required}
				bind:value
				on:focusout={validate}
				{readOnly}
			/>
		</div>
	{:else}
		<span class="flex justify-between">
			<span>
				<span>
					<slot />
				</span>
				<input
					{name}
					class={styling + (!errorMessage ? '' : ' input-error')}
					use:getType
					bind:value
					on:change={(e) => (value = e.target.checked)}
					on:focusout={validate}
					{readOnly}
				/>
			</span>

			{#if errorMessage}
				<span class="text-sm validation-error-text self-center">
					{errorMessage}
				</span>
			{:else}
				<span class="text-sm self-center"></span>
			{/if}
		</span>
	{/if}
</label>
