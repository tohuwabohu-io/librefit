<script src="+page.server.ts">
	import ValidatedInput from '$lib/components/ValidatedInput.svelte';

	/** @type {import('./$types/').ActionData} */
	export let form;
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

		<form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST">
			<ValidatedInput
				label="E-Mail"
				type="email"
				name="email"
				placeholder="Your E-Mail"
				required
			/>
			<ValidatedInput
				label="Password"
				type="password"
				name="password"
				placeholder="Your Password"
				required
			/>

			<div>
				{#if form?.error}
				<p class="variant-glass-error variant-ringed-error p-4 rounded-full">
					{form.error}
				</p>
				{/if}
			</div>

			<div>
				<button class="btn variant-filled-primary">Login</button>
			</div>
		</form>
	</div>
</section>
