<script>
    import {ProgressBar} from '@skeletonlabs/skeleton';
    import Login from '$lib/assets/icons/login.svg?component';
    import {env} from '$env/dynamic/public';
    import {goto} from '$app/navigation';

    goto('/dashboard');
</script>

<svelte:head>
    <title>LibreFit</title>
</svelte:head>

<section class="h-full flex">
    <div class="container mx-auto p-12 space-y-8 self-center">
        <div class="lg:grid lg:grid-cols-[auto_1fr_auto] xl:grid-cols-2 flex flex-col gap-4 align-middle justify-items-center">
            <div class="flex flex-col gap-4">
                <h1 class="font-logo md:flex md:flex-row lg:justify-start sm:gap-4 grid grid-cols-2 grid-rows-2 gap-1">
                    <span class="text-primary-500 md:text-9xl text-7xl text-right">Libre</span>
                    <img src="/favicon-128x128.png" role="presentation" alt="" class="max-sm:pl-4 row-span-2 self-center">
                    <span class="text-secondary-500 md:text-9xl text-7xl text-right">Fit</span>
                </h1>

                <div class="flex flex-col gap-4">
                    <p class="text-2xl">
                        Staying on track has never been easier.
                    </p>
                    <p>
                        Define goals. See progress. Build habits.
                    </p>
                    <div class="flex flex-row justify-between">
                        <p class="text-xs">
                            Your FOSS calorie tracker!
                        </p>
                        <p class="text-xs">
                            <a href="https://github.com/tohuwabohu-io/librefit">Version { env.PUBLIC_VERSION }</a>
                        </p>
                    </div>
                </div>
            </div>
            <div class="lg:w-2/3">
                <div>
                    <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST"
                          on:submit|preventDefault={handleLogin}>

                        <p>
                            { greetMsg }
                        </p>

                        <div class="flex justify-between gap-4">
                            <button class="btn variant-filled-primary" disabled={indicator.actorDisabled}>
                                <span>
                                    Login
                                </span>

                                <Login/>
                            </button>
                        </div>
                        <ProgressBar value={indicator.progress} max={100} meter={indicator.meter} track={indicator.track}/>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>