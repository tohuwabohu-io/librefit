<svelte:options accessors={true} />

<script lang="ts">
	export let value = '';
	export let name = 'control';
	export let label = 'Input';
	export let type = 'text';
	export let styling = 'input';
	export let placeholder = '';
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
	export let emptyMessage = `${label} is empty.`;
	export let required = false;

	let control, error; // bound UI elements
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
		} else {
			error.classList.add('invisible');
			control.classList.remove('input-error');
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
			<span class="self-center">
				{label}
			</span>
			<span bind:this={error} class="text-sm invisible validation-error-text self-center">
				{errorMessage}
			</span>
		</span>
		<input
			{name}
			class={styling}
			use:getType
			{placeholder}
			{required}
			bind:this={control}
			bind:value
			on:focusout={validate}
		/>
	{:else}
		<span class="flex justify-between">
			<span>
				<span>
					<slot />
				</span>
				<input {name} class={styling} use:getType bind:this={control} bind:value />
			</span>
			<span class="text-sm invisible validation-error-text self-center" bind:this={error}>
				{errorMessage}
			</span>
		</span>
	{/if}
</label>
