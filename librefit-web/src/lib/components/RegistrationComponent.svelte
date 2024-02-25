<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {applyAction, enhance} from '$app/forms';
    import {getFieldError, validateEmail, validatePassword, validatePasswordConfirmation, validateTos} from '$lib/validation.js';
    import {getModalStore} from '@skeletonlabs/skeleton';

    const modalStore = getModalStore();

    export let isModal = false;
    export let onCancel = () => {};

    /** @type {import('./$types/').ActionData} */
    export let form;

    let pwdInput;

    let status;

    const emailValidation = (e) => {
        const msg = validateEmail(e.value);

        return {
            valid: msg === null,
            errorMessage: msg
        }
    }

    const passwordValidation = (e) => {
        const msg = validatePassword(e.value);

        return {
            valid: msg === null,
            errorMessage: msg
        };
    };

    const passwordConfirmationValidation = (e) => {
        const msg = validatePasswordConfirmation(e.value, pwdInput.value);

        return {
            valid: msg === null,
            errorMessage: msg
        };
    };

    const tosValidation = (e) => {
        const msg = validateTos(e.value);

        return {
            valid: msg === null,
            errorMessage: msg
        };
    };

    const showTosModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'tosModal',
            response: () => modalStore.close()
        });
    }
</script>

<section>
    <div class="container mx-auto p-8 space-y-10">
        <h1 class="font-logo">
            <span class="text-primary-500">Sign</span>
            <span class="text-secondary-500">Up</span>
        </h1>

        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" action="?/register" use:enhance={() => {
			return async ({ result, update }) => {
			  update({ reset: false });

              status = result.data;

              console.log(status);

              await applyAction(result);
			};
		  }}>
            <ValidatedInput
                    name="email"
                    type="email"
                    placeholder="Enter E-Mail"
                    label="E-Mail"
                    required
                    validateDetail={emailValidation}
                    errorMessage={getFieldError(status, 'email')}
            />

            <ValidatedInput
                    name="password"
                    type="password"
                    placeholder="Enter Password"
                    label="Password"
                    bind:this={pwdInput}
                    required
                    validateDetail={passwordValidation}
                    errorMessage={getFieldError(status, 'password')}
            />

            <ValidatedInput
                    name="passwordConfirmation"
                    type="password"
                    placeholder="Confirm Password"
                    validateDetail={passwordConfirmationValidation}
                    label="Confirm Password"
                    required
                    errorMessage={getFieldError(status, 'passwordConfirmation')}
            />

            <label class="label">
                <span>Nickname</span>
                <input
                        name="name"
                        class="input"
                        type="text"
                        placeholder="Enter Nickname (optional)"
                />
            </label>

            <ValidatedInput
                    name="confirmation"
                    type="checkbox"
                    styling="checkbox self-center"
                    validateDetail={tosValidation}
                    errorMessage={getFieldError(status, 'confirmation')}
            >
                I agree to LibreFit's
                {#if !isModal}
                    <button class="hyperlink" on:click|preventDefault={showTosModal}>terms and conditions</button>.
                {:else}
                    <a href="/tos" target="_blank">terms and conditions</a>.
                {/if}
            </ValidatedInput>

            <div>
                {#if status?.success}
                    {#if !isModal}
                        <p class="variant-glass-success variant-ringed-success p-4 rounded-full" >
                            Successfully signed up! Please proceed to the <a href="/login">Login</a>.
                        </p>
                    {:else}
                        <p class="variant-glass-success variant-ringed-success p-4 rounded-full" >
                            Successfully signed up! You can <button class="hyperlink" on:click|preventDefault={onCancel}>close</button> this window now.
                        </p>
                    {/if}
                {:else if status?.errors}
                    <p class="variant-glass-error variant-ringed-error p-4 rounded-full">
                        Error during registration.
                    </p>
                {/if}
            </div>

            {#if !isModal}
                <div class="flex justify-between">
                    <div class="flex flex-row gap-4">
                        <a href="/login" class="self-center text-sm unstyled">Already registered?</a>
                    </div>

                    <button class="btn variant-filled-primary">Register</button>
                </div>
            {:else}
                <div class="flex justify-between">
                    <button class="btn variant-filled" on:click|preventDefault={onCancel}>Cancel</button>
                    <button class="btn variant-filled-primary">Register</button>
                </div>
            {/if}
        </form>
    </div>
</section>