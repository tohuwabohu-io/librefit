<script>
	import '../app.pcss';
	import {AppShell, Drawer, Modal, Toast} from '@skeletonlabs/skeleton';
	import TopBar from '$lib/components/TopBar.svelte';
	import WeightModal from '$lib/components/modal/WeightModal.svelte';
	import GoalModal from '$lib/components/modal/GoalModal.svelte';
	import { initializeStores } from '@skeletonlabs/skeleton';
	import UserPanel from '$lib/components/UserPanel.svelte';

	initializeStores();

	const modalComponentRegistry = {
		weightModal: {
			ref: WeightModal,
		},

		goalModal: {
			ref: GoalModal,
		}
	};

	export let data;
</script>

<Toast position={'tr'}/>
<Modal components={modalComponentRegistry} />
<Drawer position={'right'}>
	<UserPanel userData={data.userData}/>
</Drawer>

<AppShell>
	<svelte:fragment slot="header">
		{#if data.authenticated === true}
			<TopBar />
		{/if}
	</svelte:fragment>
	<!-- Router Slot -->
	<slot />
	<!-- ---- / ---- -->
	<svelte:fragment slot="pageFooter">
		<div class="text-center">
			<p class="unstyled text-xs">&copy; {new Date().getFullYear()} tohuwabohu.io</p>
		</div>
	</svelte:fragment>
</AppShell>

