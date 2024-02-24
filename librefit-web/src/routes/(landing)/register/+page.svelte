<script>
	import ValidatedInput from '$lib/components/ValidatedInput.svelte';
	import { enhance } from '$app/forms';
	import {validateEmail, validatePassword, validatePasswordConfirmation, validateTos} from '$lib/validation.js';
	import {getModalStore} from '@skeletonlabs/skeleton';

	const modalStore = getModalStore();

	/** @type {import('./$types/').ActionData} */
	export let form;

	let pwdInput;

	const emailValidation = (e) => {
		const msg = validateEmail(e.value);

		return {
			valid: msg === null,
			errorMessage: msg
		}
	}

	const passwordValidation = (e) => {
		const msg = validatePassword(e.value);

		return {
			valid: msg === null,
			errorMessage: msg
		};
	};

	const passwordConfirmationValidation = (e) => {
		const msg = validatePasswordConfirmation(e.value, pwdInput.value);

		return {
			valid: msg === null,
			errorMessage: msg
		};
	};

	const tosValidation = (e) => {
		const msg = validateTos(e.value);

		return {
			valid: msg === null,
			errorMessage: msg
		};
	};

	const showTosModal = () => {
		modalStore.trigger({
			type: 'component',
			component: 'tosModal',
			response: () => modalStore.close()
		});
	}
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
			{#if !form?.errors}
				<ValidatedInput
					name="email"
					type="email"
					placeholder="Enter E-Mail"
					label="E-Mail"
					required
					validateDetail={emailValidation}
				/>
			{:else}
				<ValidatedInput
						name="email"
						type="email"
						placeholder="Enter E-Mail"
						label="E-Mail"
						required
						validateDetail={emailValidation}
						errorMessage={form.errors['email']}
				/>
			{/if}

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
					name="name"
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
					I agree to LibreFit's <button class="hyperlink" on:click|preventDefault={showTosModal}>terms and conditions</button>.
				</ValidatedInput>
			{:else}
				<ValidatedInput
						name="confirmation"
						type="checkbox"
						styling="checkbox self-center"
						validateDetail={tosValidation}
						errorMessage={form.errors['confirmation']}
				>
					I agree to LibreFit's <button class="hyperlink" on:click|preventDefault={showTosModal}>terms and conditions.</button>.
				</ValidatedInput>
			{/if}

			<div>
				{#if form?.success}
					<p class="variant-glass-success variant-ringed-success p-4 rounded-full" >
						Successfully signed up! Please proceed to the <a href="/login">Login</a>.
					</p>
				{:else if form?.error}
					<p class="variant-glass-error variant-ringed-error p-4 rounded-full">
						{form.error}
					</p>
				{/if}
			</div>

			<div class="flex justify-between">
				<button class="btn variant-filled-primary">Register</button>

				<div class="flex flex-row gap-4">
					<a href="/login" class="self-center text-sm unstyled">Already registered?</a>
				</div>
			</div>
		</form>
	</div>
</section>
