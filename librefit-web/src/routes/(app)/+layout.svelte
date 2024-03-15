<script>
	import '../../app.pcss';
	import {AppShell, Drawer, initializeStores, Modal, Toast} from '@skeletonlabs/skeleton';
	import TopBar from '$lib/components/TopBar.svelte';
	import WeightModal from '$lib/components/modal/WeightModal.svelte';
	import GoalModal from '$lib/components/modal/GoalModal.svelte';
	import UserPanel from '$lib/components/UserPanel.svelte';
	import {setContext} from 'svelte';
	import {writable} from 'svelte/store';
	import AvatarModal from '$lib/components/modal/AvatarModal.svelte';
	import TosModal from '$lib/components/modal/TosModal.svelte';
	import {Indicator} from '$lib/indicator.js';
	import {afterNavigate, beforeNavigate} from '$app/navigation';

	initializeStores();

	const modalComponentRegistry = {
		weightModal: {
			ref: WeightModal,
		},

		goalModal: {
			ref: GoalModal,
		},

		avatarModal: {
			ref: AvatarModal
		},

		tosModal: {
			ref: TosModal
		}
	};

	export let data;

	const user = writable();
	const lastWeightTrackerEntry = writable();
	const currentGoal = writable();
	const indicator = writable();

	$: user.set(data.userData);
	$: lastWeightTrackerEntry.set(data.lastWeight);
	$: currentGoal.set(data.currentGoal);
	$: indicator.set(new Indicator());

	setContext('user', user);
	setContext('lastWeight', lastWeightTrackerEntry);
	setContext('currentGoal', currentGoal);
	setContext('indicator', indicator);

	const logout = () => {
		user.set(null);
	}

	beforeNavigate(() => {
		$indicator = $indicator.start();
	})

	afterNavigate(() => {
		$indicator = $indicator.finish();
	})
</script>

<Toast position={'tr'}/>
<Modal components={modalComponentRegistry} />
<Drawer position={'right'}>
	<UserPanel on:logout={logout}/>
</Drawer>

<AppShell>
	<svelte:fragment slot="header">
		{#if $user}
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

