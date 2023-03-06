<script>
	import '../theme.pcss';
	import '@skeletonlabs/skeleton/styles/all.css';
	import '../app.pcss';
	import { AppShell, LightSwitch } from '@skeletonlabs/skeleton';
	import TopBar from "$lib/components/TopBar.svelte";
</script>

<AppShell>
	<svelte:fragment slot="header">
		<TopBar/>
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
