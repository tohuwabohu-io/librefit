<svelte:options accessors={true} />

<script lang="ts">
	export let value = '';
	export let name = 'control';
	export let label = '';
	export let type = 'text';
	export let styling = 'input';
	export let placeholder = '';
	export let unit = '';
	export let validateDetail: () => {
		valid: boolean;
		errorMessage?: string;
		skip?: boolean;
	} = () => {
		return {
			valid: false,
			skip: true
		};
	};

	export let emptyMessage = `${label ? label : 'Field'} is empty.`;
	export let required = false;

	let control, error, unitDisplay; // bound UI elements
	let errorMessage = emptyMessage;

	const getType = (node) => {
		node.type = type;
	};

	export let validate = () => {
		let valid = false;
		let detail = validateDetail();

		if (required && isEmpty()) {
			errorMessage = emptyMessage;
		} else if (!detail.skip) {
			valid = detail.valid;
			errorMessage = detail.errorMessage;
		} else {
			valid = true;
		}

		if (!valid) {
			control.classList.add('input-error');
			error.classList.remove('invisible');

			if (unitDisplay) {
				unitDisplay.classList.add('input-error');
			}
		} else {
			error.classList.add('invisible');
			control.classList.remove('input-error');

			if (unitDisplay) {
				unitDisplay.classList.remove('input-error');
			}
		}

		return valid;
	};

	const isEmpty = () => {
		return value === undefined || value === null || value.length <= 0;
	};

	$: value;
	$: label;
	$: type;
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
			<span bind:this={error} class="text-sm invisible validation-error-text self-center">
				{errorMessage}
			</span>
		</span>
		<div class={!unit ? '' : 'input-group input-group-divider grid-cols-[auto_1fr_auto]'}>
			{#if unit}
				<div class="input-group-shim" bind:this={unitDisplay}>{unit}</div>
			{/if}
			<input
				{name}
				class={styling + (!unit ? '' : 'rounded-none')}
				use:getType
				{placeholder}
				{required}
				bind:this={control}
				bind:value
				on:focusout={validate}
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
					class={styling}
					use:getType
					bind:value
					bind:this={control}
					on:change={(e) => (value = e.target.checked)}
					on:focusout={validate}
				/>
			</span>
			<span class="text-sm invisible validation-error-text self-center" bind:this={error}>
				{errorMessage}
			</span>
		</span>
	{/if}
</label>
