<script>
    import {page} from '$app/stores';
    import {Avatar, getDrawerStore} from '@skeletonlabs/skeleton';
    import {goto} from '$app/navigation';
    import {createEventDispatcher, getContext} from 'svelte';

    const drawerStore = getDrawerStore();

    const user = getContext('user');
    $: user;

    $: classesActive = (href) => (href === $page.url.pathname ? '!bg-primary-500' : '');

    const dispatch = createEventDispatcher();

    export const logout = (e) => {
        goto('/logout');
        dispatch('logout');

        drawerStore.close();
    }
</script>

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
                <a href="/" class="{classesActive('/')}" on:click={() => drawerStore.close()}>
                    Dashboard
                </a>
            </li>
            <li>
                <a href="/profile" class="{classesActive('/profile')}" on:click={() => drawerStore.close()}>
                    Profile
                </a>
            </li>
            <li>
                <a href="/wizard" class="{classesActive('/wizard')}" on:click={() => drawerStore.close()}>
                    Wizard
                </a>
            </li>
            <li>
                <a href="/tracker/calories" class="{classesActive('/tracker/calories')}" on:click={() => drawerStore.close()}>
                    Calorie Tracker
                </a>
            </li>
            <li>
                <a href="/tracker/weight" class="{classesActive('/tracker/weight')}" on:click={() => drawerStore.close()}>
                    Weight Tracker
                </a>
            </li>
            <li>
                <a href="https://github.com/tohuwabohu-io/librefit" target="_blank">
                    GitHub
                </a>
            </li>
        </ul>
    </nav>

    <button data-sveltekit-reload class="btn variant-filled-secondary" on:click|preventDefault={logout}>Log out</button>
</div>