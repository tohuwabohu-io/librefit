<script>
	import '../theme.pcss';
	import '@skeletonlabs/skeleton/styles/all.css';
	import '../app.pcss';
	import { AppShell, LightSwitch } from '@skeletonlabs/skeleton';
</script>

<AppShell>
	<svelte:fragment slot="header">
		<a href="/">Home</a>
		<a href="/login">Login</a>
		<a href="/register">Registration</a>
		<a href="/wizard">Wizard</a>
		<a href="/dashboard">Dashboard</a>
	</svelte:fragment>
	<!-- Router Slot -->
	<slot />
	<!-- ---- / ---- -->
	<svelte:fragment slot="footer">
		<div class="text-center">
			<p>&copy; {new Date().getFullYear()} tohuwabohu.io</p>
		</div>
	</svelte:fragment>
</AppShell>
