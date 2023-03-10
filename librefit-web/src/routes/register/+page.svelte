<script>
	import ValidatedInput from '$lib/components/ValidatedInput.svelte';
	import { Configuration, UserResourceApi } from 'librefit-api/rest';
	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import { toastStore } from '@skeletonlabs/skeleton';

	let emailInput, passwordInput, passwordConfirmationInput, tosInput, registrationButton;
	let registrationData = {
		name: '',
		email: '',
		password: ''
	};

	let passwordConfirmation;
	let tosAccepted = false;

	const userApi = new UserResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const register = async () => {
		if (
			![emailInput, passwordInput, passwordConfirmationInput, tosInput]
				.map((control) => control.validate())
				.includes(false)
		) {
			await userApi
				.userRegisterPost(registrationData)
				.then(() => {
					toastStore.trigger({
						message: 'Successfully signed up!',
						classes: 'variant-filled-success'
					});
				})
				.catch((error) => {
					toastStore.trigger({
						message: 'An error occured.',
						classes: 'variant-glass-error'
					});

					console.error(error);
				});
		}
	};

	const passwordValidation = () => {
		// some arbitrary rules
		if (passwordInput.value.indexOf('a') < 0) {
			return {
				valid: false,
				errorMessage: "Chosen password must contain at least one 'a' letter."
			};
		}

		return { valid: true };
	};

	const passwordConfirmationValidation = () => {
		if (passwordInput.value !== passwordConfirmation) {
			return {
				valid: false,
				errorMessage: 'Passwords do not match.'
			};
		}

		return { valid: true };
	};

	const tosValidation = () => {
		if (!tosAccepted) {
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

		<form class="variant-ringed p-4 space-y-4 rounded-container-token">
			<ValidatedInput
				name="email"
				type="email"
				placeholder="Enter E-Mail"
				label="E-Mail"
				required
				bind:this={emailInput}
				bind:value={registrationData.email}
			/>

			<ValidatedInput
				name="password"
				type="password"
				placeholder="Enter Password"
				label="Password"
				required
				bind:this={passwordInput}
				validateDetail={passwordValidation}
				bind:value={registrationData.password}
			/>
			<ValidatedInput
				name="passwordConfirmation"
				type="password"
				placeholder="Confirm Password"
				validateDetail={passwordConfirmationValidation}
				label="Confirm Password"
				required
				bind:this={passwordConfirmationInput}
				bind:value={passwordConfirmation}
			/>

			<label class="label">
				<span>Nickname</span>
				<input
					name="username"
					class="input"
					type="text"
					placeholder="Enter Nickname (optional)"
					bind:value={registrationData.name}
				/>
			</label>

			<ValidatedInput
				name="confirmation"
				type="checkbox"
				styling="checkbox self-center"
				bind:this={tosInput}
				bind:value={tosAccepted}
				validateDetail={tosValidation}
			>
				I agree to LibreFit's terms and conditions.
			</ValidatedInput>

			<div class="flex justify-between">
				<button
					bind:this={registrationButton}
					on:click={register}
					class="btn variant-filled-primary">Register</button
				>

				<div class="flex flex-row gap-4">
					<p class="self-center text-sm unstyled">
						Already registered?
					</p>
					<a href="/login" class="btn variant-filled-secondary cursor-pointer">
						Login
					</a>
				</div>
			</div>
		</form>
	</div>
</section>
