<script>
    import {getFieldError} from '$lib/validation.js';
    import {getModalStore, ProgressBar} from '@skeletonlabs/skeleton';
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import Login from '$lib/assets/icons/login.svg?component';
    import {Indicator} from '$lib/indicator.js';
    import {goto} from '$app/navigation';
    import {getContext} from 'svelte';
    import {login} from '$lib/api/user.js';
    import {page} from '$app/stores';
    import { env } from '$env/dynamic/public';

    const modalStore = getModalStore();

    const user = getContext('user');

    let indicator = new Indicator();
    let status;

    const expired = $page.url.searchParams.get('expired');

    if (expired) {
        status = {
            errors: [{
                field: 'email', message: 'Your session expired. Please login again.'
            }]
        }
    }

    const handleLogin = async (event) => {
        status = undefined;

        indicator = indicator.reset();
        indicator = indicator.start();

        const formData = new FormData(event.currentTarget);

        await login(formData).then(async response => {
            if (response.ok) {
                user.set(await response.json());
                indicator = indicator.finishSuccess();

                await goto('/dashboard');
            } else {
                throw response;
            }
        }).catch(e => {
            indicator = indicator.finishError();

            console.error(e);

            status = e.data;

            if (!status) {
                status = {errors: [{field: 'email', message: 'An error occurred. Please try again later.' }]}
            }
        });
    }

    const showRegisterModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'registrationModal',
            response: (e) => {
                if (!e) modalStore.close()
            }
        });
    }
</script>

<svelte:head>
    <title>LibreFit</title>
</svelte:head>

<section class="variant-ghost-surface h-full flex">
    <div class="container mx-auto p-12 space-y-8 self-center">
        <div class="md:grid md:grid-cols-[auto_1fr_auto] xl:grid-cols-2 flex flex-col gap-4 align-middle justify-items-center">
            <div class="flex flex-col gap-4">
                <h1 class="font-logo flex flex-row md:justify-start md:gap-4">
                    <span class="text-primary-500 lg:text-9xl md:text-8xl text-7xl">Libre</span>
                    <span class="text-secondary-500 lg:text-9xl md:text-8xl text-7xl">Fit</span>
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
                        <ValidatedInput
                                label="E-Mail"
                                type="email"
                                name="email"
                                placeholder="Your E-Mail"
                                required
                                errorMessage={getFieldError(status, 'email')}
                        />
                        <ValidatedInput
                                label="Password"
                                type="password"
                                name="password"
                                placeholder="Your Password"
                                required
                        />

                        <div class="flex justify-between gap-4">
                            <button class="btn variant-filled-primary" disabled={indicator.actorDisabled}>
                                <span>
                                    Login
                                </span>

                                <Login/>
                            </button>

                            <div class="flex flex-row gap-4">
                                <button class="hyperlink self-center text-sm"
                                        on:click|preventDefault={showRegisterModal}>Not signed up yet?
                                </button>
                            </div>
                        </div>
                        <ProgressBar value={indicator.progress} max={100} meter={indicator.meter} track={indicator.track}/>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>