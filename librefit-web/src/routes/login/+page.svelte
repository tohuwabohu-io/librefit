<script>
	/** @type {import('./$types').ActionData} */ export let form;
</script>

<svelte:head>
	<title>LibreFit - Login</title>
</svelte:head>

<section>
	<div class="container mx-auto p-8 space-y-10">
		<h1>Login</h1>

		<form method="POST" class="variant-ringed p-4 space-y-4 rounded-container-token">
			<label class="label">
				<span>E-Mail</span>
				<input name="username" class="input" type="email" placeholder="Your E-Mail" required />
			</label>
			<label class="label">
				<span>Password</span>
				<input name="password" class="input" type="password" placeholder="Your Password" required />
			</label>
			<div>
				<button class="btn variant-filled-primary">Login</button>
			</div>
		</form>
	</div>
</section>

{#if form?.success}
	<p>Login successful! Welcome!</p>
{/if}

{#if form && !form?.success}
	<p>Error during login.</p>
{/if}
