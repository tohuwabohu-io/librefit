<script>
	import ValidatedInput from '$lib/components/ValidatedInput.svelte';
	import { enhance } from '$app/forms';

	/** @type {import('./$types/').ActionData} */
	export let form;

	let pwdInput;

	const passwordValidation = (e) => {
		// some arbitrary rules
		if (e.value.indexOf('a') < 0) {
			return {
				valid: false,
				errorMessage: "Chosen password must contain at least one 'a' letter."
			};
		}

		return { valid: true };
	};

	const passwordConfirmationValidation = (e) => {
		if (e.value !== pwdInput.value) {
			return {
				valid: false,
				errorMessage: 'Passwords do not match.'
			};
		}

		return { valid: true };
	};

	const tosValidation = (e) => {
		if (!e.value) {
			return {
				valid: false,
				errorMessage: 'Please accept our terms and conditions.'
			};
		}

		return { valid: true };
	};
</script>

<svelte:head>
	<title>LibreFit - Registration</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<h1 class="font-logo">
			<span class="text-primary-500">Sign</span>
			<span class="text-secondary-500">Up</span>
		</h1>

		<form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" use:enhance={() => {
			return async ({ update }) => {
			  update({ reset: false });
			};
		  }}>
			<ValidatedInput
				name="email"
				type="email"
				placeholder="Enter E-Mail"
				label="E-Mail"
				required
			/>

			{#if !form?.errors}
				<ValidatedInput
					name="password"
					type="password"
					placeholder="Enter Password"
					label="Password"
					bind:this={pwdInput}
					required
					validateDetail={passwordValidation}
				/>
			{:else}
				<ValidatedInput
						name="password"
						type="password"
						placeholder="Enter Password"
						label="Password"
						bind:this={pwdInput}
						required
						validateDetail={passwordValidation}
						errorMessage={form.errors['password']}
				/>
			{/if}

			{#if !form?.errors}
				<ValidatedInput
					name="passwordConfirmation"
					type="password"
					placeholder="Confirm Password"
					validateDetail={passwordConfirmationValidation}
					label="Confirm Password"
					required
				/>
			{:else}
				<ValidatedInput
						name="passwordConfirmation"
						type="password"
						placeholder="Confirm Password"
						validateDetail={passwordConfirmationValidation}
						label="Confirm Password"
						required
						errorMessage={form.errors['passwordConfirmation']}
				/>
			{/if}

			<label class="label">
				<span>Nickname</span>
				<input
					name="username"
					class="input"
					type="text"
					placeholder="Enter Nickname (optional)"
				/>
			</label>

			{#if !form?.errors}
				<ValidatedInput
					name="confirmation"
					type="checkbox"
					styling="checkbox self-center"
					validateDetail={tosValidation}
				>
					I agree to LibreFit's terms and conditions.
				</ValidatedInput>
			{:else}
				<ValidatedInput
						name="confirmation"
						type="checkbox"
						styling="checkbox self-center"
						validateDetail={tosValidation}
						errorMessage={form.errors['confirmation']}
				>
					I agree to LibreFit's terms and conditions.
				</ValidatedInput>
			{/if}

			<div>
				{#if form?.success}
					<p class="variant-glass-success variant-ringed-success p-4 rounded-full hidden" >
						Successfully signed up! Please proceed to the <a href="/login">Login</a>.
					</p>
				{:else if form?.error}
					<p class="variant-glass-error variant-ringed-error p-4 rounded-full hidden">
						{form.error}
					</p>
				{/if}
			</div>

			<div class="flex justify-between">
				<button class="btn variant-filled-primary">Register</button>

				<div class="flex flex-row gap-4">
					<p class="self-center text-sm unstyled">Already registered?</p>
					<a href="/login" class="btn variant-filled-secondary cursor-pointer"> Login </a>
				</div>
			</div>
		</form>
	</div>
</section>
