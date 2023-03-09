<script>
	import {Configuration, UserResourceApi} from 'librefit-api/rest';

	import { PUBLIC_API_BASE_PATH } from '$env/static/public';
	import {toastStore} from "@skeletonlabs/skeleton";

	let loginButton, emailError, passwordError;

	const loginData = {
		email: '', password: ''
	};

	const userApi = new UserResourceApi(
		new Configuration({
			basePath: PUBLIC_API_BASE_PATH
		})
	);

	const login = async () => {
		if (validateField(loginData.email, emailError) &&
			validateField(loginData.password, passwordError)) {

			// TODO: disable login button, re-enable in finally block.

			await userApi.userLoginPost(loginData).then(() => {
				toastStore.trigger({
					message: 'Login successful!',
					autohide: false,
					classes: 'variant-filled-primary' // TODO there seems to be a bug in Toast - secondary is displayed
				});
			}).catch((e) => {
				let errorMessage = 'Error during login';

				if (e.status === 404) {
					errorMessage = 'Invalid username or password.'
				}

				toastStore.trigger({
					message: errorMessage,
					autohide: false,
					classes: 'variant-filled-error'
				});
			});
		}
	}

	const validateField = (field, errorComponent) => {
		let invalid = !field || field.length <= 0;

		if (invalid) {
			errorComponent.classList.remove('invisible');
		} else {
			errorComponent.classList.add('invisible');
		}

		return !invalid;
	}
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
			<label class="label">
					<span>E-Mail</span>
				<input name="username" class="input" type="email" placeholder="Your E-Mail" required
					bind:value={loginData.email} on:focusout={validateField(loginData.email, emailError)} />
				<span bind:this={emailError} class="text-sm invisible validation-error-text">Please enter your E-Mail.</span>
			</label>
			<label class="label">
				<span>Password</span>
				<input name="password" class="input" type="password" placeholder="Your Password" required
					bind:value={loginData.password} on:focusout={validateField(loginData.password, passwordError)}/>
				<span bind:this={passwordError} class="text-sm invisible validation-error-text">Please enter your password.</span>
			</label>
			<div>
				<button bind:this={loginButton} on:click={login} class="btn variant-filled-primary">Login</button>
			</div>
		</form>
	</div>
</section>
