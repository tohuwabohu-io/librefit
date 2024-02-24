<script>

	import '../../app.pcss';
	import {AppShell, initializeStores, Modal} from '@skeletonlabs/skeleton';
	import TosModal from '$lib/components/modal/TosModal.svelte';

	initializeStores();

	const modalComponentRegistry = {
		tosModal: {
			ref: TosModal
		}
	};
</script>

<Modal components={modalComponentRegistry} />

<AppShell>
	<!-- Router Slot -->
	<slot />
	<!-- ---- / ---- -->
</AppShell>

