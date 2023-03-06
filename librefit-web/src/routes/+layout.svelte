<script>
	import '../theme.pcss';
	import '@skeletonlabs/skeleton/styles/all.css';
	import '../app.pcss';
	import { AppShell, LightSwitch } from '@skeletonlabs/skeleton';

	let isDark = false;
	let opened = false;

	function toggleTheme() {
		isDark = !isDark;
	}
	function toggleOpened() {
		opened = !opened;
	}
</script>

<AppShell>
	<svelte:fragment slot="header">
		<a href="/">Home</a>
		<a href="/login">Login</a>
		<a href="/register">Registration</a>
		<a href="/wizard">Wizard</a>
		<a href="/dashboard">Dashboard</a>
		<LightSwitch />
	</svelte:fragment>
	<!-- <svelte:fragment slot="sidebarLeft">Sidebar Left</svelte:fragment>
	<svelte:fragment slot="sidebarRight">Sidebar Right</svelte:fragment> -->
	<svelte:fragment slot="pageHeader">Page Header</svelte:fragment>
	<!-- Router Slot -->
	<slot />
	<!-- ---- / ---- -->
	<svelte:fragment slot="pageFooter">Page Footer</svelte:fragment>
	<svelte:fragment slot="footer">Footer</svelte:fragment>
</AppShell>
