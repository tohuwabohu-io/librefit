<script>
    import '../app.pcss';
    import {autoModeWatcher, AppShell, Drawer, initializeStores, Modal, Toast} from '@skeletonlabs/skeleton';
    import TopBar from '$lib/components/TopBar.svelte';
    import WeightModal from '$lib/components/modal/WeightModal.svelte';
    import GoalModal from '$lib/components/modal/GoalModal.svelte';
    import UserPanel from '$lib/components/UserPanel.svelte';
    import {onMount, setContext} from 'svelte';
    import {writable} from 'svelte/store';
    import AvatarModal from '$lib/components/modal/AvatarModal.svelte';
    import TosModal from '$lib/components/modal/TosModal.svelte';
    import {Indicator} from '$lib/indicator.js';
    import {afterNavigate, beforeNavigate} from '$app/navigation';
    import RegistrationModal from '$lib/components/modal/RegistrationModal.svelte';
    import CalorieTrackerModal from '$lib/components/modal/CalorieTrackerModal.svelte';
    import {observeToggle} from '$lib/theme-toggle.js';

    initializeStores();

	onMount(() => {
		autoModeWatcher();
	});

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
        },

        registrationModal: {
            ref: RegistrationModal
        },

        trackerModal: {
            ref: CalorieTrackerModal
        }
    };

    const user = writable();
    const indicator = writable();
    const currentGoal = writable();
    const lastWeight = writable();
    const foodCategories = writable();
    const ctList = writable();

    $: indicator.set(new Indicator());

    setContext('user', user);
    setContext('indicator', indicator);
    setContext('currentGoal', currentGoal);
    setContext('lastWeight', lastWeight);
    setContext('foodCategories', foodCategories);
    setContext('ctList', ctList);

    const logout = () => {
        user.set(null);
    }

    beforeNavigate(() => {
        $indicator = $indicator.start();
    })

    afterNavigate(() => {
        $indicator = $indicator.finish();

        setTimeout(() => {
            $indicator = $indicator.hide();
        }, 1000);
    })

    observeToggle(document.documentElement, (document) => {
        if (document.classList.contains('dark')) {
            $indicator = $indicator.toggle('dark');
        } else {
            $indicator = $indicator.toggle('light');
        }
    });
</script>

<Toast position={'tr'}/>
<Modal components={modalComponentRegistry}/>
<Drawer position={'right'}>
    <UserPanel on:logout={logout}/>
</Drawer>

<AppShell>
    <svelte:fragment slot="header">
        {#if $user && window.location.pathname !== '/'}
            <TopBar/>
        {/if}
    </svelte:fragment>
    <!-- Router Slot -->
    <slot/>
    <!-- ---- / ---- -->
    <svelte:fragment slot="pageFooter">
        {#if $user && window.location.pathname !== '/'}
            <div class="text-center">
                <p class="unstyled text-xs">&copy; {new Date().getFullYear()} tohuwabohu.io</p>
            </div>
        {/if}
    </svelte:fragment>
</AppShell>
