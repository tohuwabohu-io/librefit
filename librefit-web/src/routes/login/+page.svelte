<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {goto} from '$app/navigation';
    import {getFieldError} from '$lib/validation.js';
    import Login from '$lib/assets/icons/login.svg?component';
    import {ProgressBar} from '@skeletonlabs/skeleton';
    import {getContext} from 'svelte';
    import {Indicator} from '$lib/indicator.js';
    import {login} from '$lib/api/user.js';

    const user = getContext('user');

    let indicator = new Indicator();
    let status;

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
        });
    }
</script>

<svelte:head>
    <title>LibreFit - Login</title>
</svelte:head>

<section>
    <div class="container mx-auto p-8 space-y-10">
        <h1 class="font-logo">
            <span class="text-primary-500">Log</span>
            <span class="text-secondary-500">in</span>
        </h1>

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
            </div>
            <ProgressBar value={indicator.progress} max={100} meter={indicator.meter} track={indicator.track}/>
        </form>
    </div>
</section>
