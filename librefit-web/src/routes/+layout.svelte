<script>
	import '../app.pcss';
	import {AppShell, Modal, Toast} from '@skeletonlabs/skeleton';
	import TopBar from '$lib/components/TopBar.svelte';
	import WeightModal from '$lib/components/modal/WeightModal.svelte';
	import GoalModal from '$lib/components/modal/GoalModal.svelte';
	import { initializeStores } from '@skeletonlabs/skeleton';

	initializeStores();

	const modalComponentRegistry = {
		weightModal: {
			ref: WeightModal,
		},

		goalModal: {
			ref: GoalModal,
		}
	};
</script>

<AppShell>
	<svelte:fragment slot="header">
		<TopBar />
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

<Toast position={'tr'}/>
<Modal components={modalComponentRegistry} />