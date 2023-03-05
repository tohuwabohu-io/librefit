<script>
	/** @type {import('./$types').ActionData} */ export let form;
</script>

<svelte:head>
	<title>LibreFit - Registration</title>
</svelte:head>

<div>
	<h1>Registration</h1>

	<form method="POST">
		<label>
			<span>
				Nickname
			</span>

			<input class="input" type="text" name="username">
		</label>

		<label>
			<span>
				E-Mail
			</span>

			<input class="input" type="text" name="email" required>
		</label>

		<label>
			<span>
				Password
			</span>

			<input class="input" type="password" name="password" required>
		</label>

		<label>
			<span>
				I agree to LibreFit's terms and conditions.
			</span>
			<input class="input" type="checkbox" name="confirmation">
		</label>

		<button>Register</button>
	</form>

	{#if form && !form.success}
		<p>An error occured during registration.</p>
	{/if}
</div>
