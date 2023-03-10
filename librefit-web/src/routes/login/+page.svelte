<script>
	import { Configuration, UserResourceApi } from 'librefit-api/rest';

	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import { toastStore } from '@skeletonlabs/skeleton';
	import ValidatedInput from '$lib/components/ValidatedInput.svelte';

	let loginButton, emailInput, passwordInput;

	const loginData = {
		email: '',
		password: ''
	};

	const userApi = new UserResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const login = async () => {
		if (![emailInput, passwordInput].map((control) => control.validate()).includes(false)) {
			// TODO: disable login button, re-enable in finally block.

			await userApi
				.userLoginPost(loginData)
				.then(() => {
					toastStore.trigger({
						message: 'Login successful!',
						autohide: false,
						classes: 'variant-filled-primary' // TODO there seems to be a bug in Toast - secondary is displayed
					});
				})
				.catch((e) => {
					let errorMessage = 'Error during login';

					if (e.response.status === 404) {
						errorMessage = 'Invalid username or password.';
					}

					toastStore.trigger({
						message: errorMessage,
						autohide: false,
						classes: 'variant-filled-error'
					});
				});
		}
	};
</script>

<svelte:head>
	<title>LibreFit - Login</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<h1 class="font-logo">
			<span class="text-primary-500">Log</span>
			<span class="text-secondary-500">in</span>
		</h1>

		<form class="variant-ringed p-4 space-y-4 rounded-container-token">
			<ValidatedInput
				label="E-Mail"
				type="email"
				name="email"
				placeholder="Your E-Mail"
				required
				bind:value={loginData.email}
				bind:this={emailInput}
			/>
			<ValidatedInput
				label="Password"
				type="password"
				name="password"
				placeholder="Your Password"
				required
				bind:value={loginData.password}
				bind:this={passwordInput}
			/>
			<div>
				<button bind:this={loginButton} on:click={login} class="btn variant-filled-primary"
					>Login</button
				>
			</div>
		</form>
	</div>
</section>
