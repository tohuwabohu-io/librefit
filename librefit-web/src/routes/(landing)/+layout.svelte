<script>

	import '../../app.pcss';
	import {AppShell, initializeStores, Modal} from '@skeletonlabs/skeleton';
	import TosModal from '$lib/components/modal/TosModal.svelte';
	import RegistrationModal from '$lib/components/modal/RegistrationModal.svelte';

	initializeStores();

	const modalComponentRegistry = {
		tosModal: {
			ref: TosModal
		},
		registrationModal: {
			ref: RegistrationModal
		}
	};
</script>

<Modal components={modalComponentRegistry} />

<AppShell>
	<!-- Router Slot -->
	<slot />
	<!-- ---- / ---- -->
</AppShell>

