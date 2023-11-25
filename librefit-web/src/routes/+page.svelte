<script>
    import {setContext} from 'svelte';
    import {writable} from 'svelte/store';
    import ArrowRight from '$lib/assets/icons/arrow-right.svg?component';
    import Login from '$lib/assets/icons/login.svg?component';
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';

    const goal = writable();

    $: goal.set({
        targetCalories: 1923,
        maximumCalories: 2401
    });

    setContext('currentGoal', goal);

    /** @type {import('./$types/').ActionData} */
    export let form;
</script>

<svelte:head>
    <title>LibreFit</title>
</svelte:head>

<section>
    <div class="mx-auto p-12 space-y-8 variant-ghost-surface">
        <div class="container">
            <div class="grid grid-cols-[auto_1fr_auto] xl:grid-cols-2 gap-4">
                <div class="flex flex-col gap-4">
                    <h1 class="font-logo">
                        <span class="text-primary-500 text-9xl">Libre</span>
                        <span class="text-secondary-500 text-9xl">Fit</span>
                    </h1>

                    <div class="flex flex-col gap-4">
                        <p class="text-2xl">
                            Staying on track has never been easier.
                        </p>
                        <p>
                            Define goals. See progress. Build habits.
                        </p>
                        <p class="text-xs">
                            Your FOSS calorie tracker!
                        </p>
                    </div>


                </div>
                <div class="w-2/3">
                    <div>
                        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST">
                            <ValidatedInput
                                    label="E-Mail"
                                    type="email"
                                    name="email"
                                    placeholder="Your E-Mail"
                                    required
                            />
                            <ValidatedInput
                                    label="Password"
                                    type="password"
                                    name="password"
                                    placeholder="Your Password"
                                    required
                            />

                            <div>
                                {#if form?.error}
                                    <p class="variant-glass-error variant-ringed-error p-4 rounded-full">
                                        {form.error}
                                    </p>
                                {/if}
                            </div>

                            <div class="flex justify-between">
                                <button class="btn variant-filled-primary">
                                    <span>
                                        Login
                                    </span>

                                    <Login/>
                                </button>

                                <div class="flex flex-row gap-4">
                                    <p class="self-center text-sm unstyled">Not signed up yet?</p>
                                    <a href="/register" class="btn variant-filled-secondary">
                                    <span>
                                        Register
                                    </span>

                                        <ArrowRight/>
                                    </a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
