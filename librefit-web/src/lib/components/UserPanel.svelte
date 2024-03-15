<script>
    import {page} from '$app/stores';
    import {Avatar, getDrawerStore} from '@skeletonlabs/skeleton';
    import {goto} from '$app/navigation';
    import {createEventDispatcher, getContext} from 'svelte';
    import Dashboard from '$lib/assets/icons/dashboard.svg?component';
    import Wand from '$lib/assets/icons/wand.svg?component';
    import User from '$lib/assets/icons/user.svg?component';
    import GitHub from '$lib/assets/icons/github.svg?component';
    import Food from '$lib/assets/icons/food.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import FileTypeCsv from '$lib/assets/icons/file-type-csv.svg';

    const drawerStore = getDrawerStore();

    const user = getContext('user');
    $: user;

    const indicator = getContext('indicator');
    $: indicator;

    $: classesActive = (href) => (href === $page.url.pathname ? '!bg-primary-500' : '');

    const dispatch = createEventDispatcher();

    const logout = (e) => {
        drawerStore.close();
        goto('/logout')

        dispatch('logout');
    }

    const navigate = () => {
        drawerStore.close();
    }
</script>

{#if $user}
<div class="container mx-auto p-8 space-y-10">
    <div class="flex flex-row gap-6">
        <span>
            <Avatar src={$user.avatar} initials="LU" width="w-16"/>
        </span>
        <div class="flex flex-col justify-evenly">
            <p>
                {$user.email}
            </p>
            <p>
                {$user.name}
            </p>
        </div>
    </div>

    <nav class="list-nav">
        <ul>
            <li>
                <a href="/dashboard" class="{classesActive('/dashboard')}" on:click={navigate}>
                    <span><Dashboard/></span>
                    <span class="flex-auto">
                        Dashboard
                    </span>
                </a>
            </li>
            <li>
                <a href="/profile" class="{classesActive('/profile')}" on:click={navigate}>
                    <span><User/></span>
                    <span class="flex-auto">
                        Profile
                    </span>
                </a>
            </li>
            <li>
                <a href="/wizard" class="{classesActive('/wizard')}" on:click={navigate}>
                    <span><Wand/></span>
                    <span class="flex-auto">
                        Wizard
                    </span>
                </a>
            </li>
            <li>
                <a href="/tracker/calories" class="{classesActive('/tracker/calories')}" on:click={navigate}>
                    <span><Food/></span>
                    <span class="flex-auto">
                        Calorie Tracker
                    </span>
                </a>
            </li>
            <li>
                <a href="/tracker/weight" class="{classesActive('/tracker/weight')}" on:click={navigate}>
                    <span><Scale/></span>
                    <span class="flex-auto">
                        Weight Tracker
                    </span>
                </a>
            </li>
            <li>
                <a href="/import" class="{classesActive('/import')}" on:click={navigate}>
                    <span><FileTypeCsv/></span>
                    <span class="flex-auto">
                        Import data
                    </span>
                </a>
            </li>
            <li>
                <a href="https://github.com/tohuwabohu-io/librefit" target="_blank">
                    <span><GitHub/></span>
                    <span class="flex-auto">
                        GitHub
                    </span>
                </a>
            </li>
        </ul>
    </nav>

    <button data-sveltekit-reload class="btn variant-filled-secondary" on:click|preventDefault={logout}>Log out</button>
</div>
{/if}