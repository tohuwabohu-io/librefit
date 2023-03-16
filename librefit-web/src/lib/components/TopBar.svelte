<script>
	import { AppBar } from '@skeletonlabs/skeleton';
</script>

<AppBar shadow="drop-shadow">
	<svelte:fragment slot="lead">
		<a href="/" class="font-logo text-2xl">
			<span class="text-primary-500"> Libre </span>
			<span class="text-secondary-500"> Fit </span>
		</a>
	</svelte:fragment>
	<svelte:fragment slot="trail">
		<a href="/login">Login</a>
		<a href="/register">Registration</a>
		<a href="/wizard">Wizard</a>
		<a href="/dashboard">Dashboard</a>
		<a href="/tracker">Tracker</a>
	</svelte:fragment>
</AppBar>
