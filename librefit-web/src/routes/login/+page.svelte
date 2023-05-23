<script>
	import {UserResourceApi} from 'librefit-api/rest';

	import ValidatedInput from '$lib/components/ValidatedInput.svelte';
	import {AUTH_TOKEN_KEY, DEFAULT_CONFIG} from '$lib/api/Config.js';

	let loginButton, emailInput, passwordInput;
	let success, error, errorText;

	const loginData = {
		email: '',
		password: ''
	};

	const userApi = new UserResourceApi(DEFAULT_CONFIG);

	const login = async () => {
		if (![emailInput, passwordInput].map((control) => control.validate()).includes(false)) {
			// TODO: disable login button, re-enable in finally block.

			await userApi
				.userLoginPost({ libreUser: loginData })
				.then((response) => {
					// TODO redirect
					success.classList.remove('hidden');
					error.classList.add('hidden');

					console.log(response);

					localStorage.setItem(AUTH_TOKEN_KEY, response.token);
				})
				.catch((e) => {
					errorText = 'Error during login';

					if (e.response.status === 404) {
						errorText = 'Invalid username or password.';
					}

					success.classList.add('hidden');
					error.classList.remove('hidden');
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
				<p
					bind:this={success}
					class="variant-glass-success variant-ringed-success p-4 rounded-full hidden"
				>
					Successfully logged in!
				</p>
				<p
					bind:this={error}
					class="variant-glass-error variant-ringed-error p-4 rounded-full hidden"
				>
					{errorText}
				</p>
			</div>

			<div>
				<button
					bind:this={loginButton}
					on:click|preventDefault={login}
					class="btn variant-filled-primary">Login</button
				>
			</div>
		</form>
	</div>
</section>
