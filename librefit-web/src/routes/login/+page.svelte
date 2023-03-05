<script>
	/** @type {import('./$types').ActionData} */ export let form;
</script>

<svelte:head>
	<title>LibreFit - Login</title>
</svelte:head>

<div>
	<h1>Login</h1>

	<form method="POST">
		<label class="label">
			<span> E-Mail </span>
			<input class="input" type="text" name="username" />
		</label>

		<label class="label">
			<span> Password </span>

			<input class="input" type="password" name="password" />
		</label>

		<button class="button">Login</button>
	</form>
</div>

{#if form?.success}
	<p>Login successful! Welcome!</p>
{/if}

{#if form && !form?.success}
	<p>Error during login.</p>
{/if}
