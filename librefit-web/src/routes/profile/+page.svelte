<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import { enhance } from '$app/forms';
    import {getContext} from 'svelte';
    import {validatePassword, validatePasswordConfirmation} from '$lib/validation.js';
    import {Avatar, getModalStore} from '@skeletonlabs/skeleton';

    const user = getContext('user');
    $: user;

    const modalStore = getModalStore();

    /** @type {import('./$types/').ActionData} */
    export let form;

    let pwdInput;
    let avatarInput;
    let selectedAvatar = $user.avatar;

    const showAvatarPickerModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'avatarModal',
            response: (e) => {
                if (!e.cancelled) {
                    selectedAvatar = e.avatar;
                }

                modalStore.close();
            }
        });
    }

</script>

<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1>Profile</h1>
        <h2>Change your user settings.</h2>

        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" use:enhance={() => {
			return async ({ update }) => {
			  update({ reset: false });
			};
		  }}>

            <ValidatedInput readOnly={true}
                    value={$user.email}
                    name="email"
                    type="email"
                    placeholder="Enter E-Mail"
                    label="E-Mail"
            />

            <label class="label">
                <span>Nickname</span>
                <input
                        value={$user.name}
                        name="username"
                        class="input"
                        type="text"
                        placeholder="Enter Nickname (optional)"
                />
            </label>

            <div class="flex flex-col gap-4">
                <span>Avatar</span>

                <div class="flex flex-row gap-4 items-center">
                    <Avatar src={selectedAvatar} initials="LU"/>

                    <div class="justify-center">
                        <button on:click|preventDefault={showAvatarPickerModal}
                                class="btn variant-filled-secondary">Change</button>
                    </div>
                    <input bind:this={avatarInput} bind:value={selectedAvatar} name="avatar" type="text" style="visibility:hidden"/>
                </div>

            </div>

            {#if !form?.errors}
                <ValidatedInput
                        name="currentPassword"
                        type="password"
                        placeholder="Enter Password"
                        label="Current Password"
                        bind:this={pwdInput}
                        required
                />
            {:else}
                <ValidatedInput
                        name="currentPassword"
                        type="password"
                        placeholder="Enter Password"
                        label="Current password"
                        bind:this={pwdInput}
                        required
                        errorMessage={form.errors['password']}
                />
            {/if}

            <div class="flex justify-between">
                <button class="btn variant-filled-primary">Update</button>
            </div>
        </form>
    </div>
</section>