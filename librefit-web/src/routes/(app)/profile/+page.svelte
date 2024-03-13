<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import { enhance } from '$app/forms';
    import {getContext} from 'svelte';
    import {Avatar, getModalStore, getToastStore} from '@skeletonlabs/skeleton';
    import { showToastError, showToastSuccess, showToastInfo} from '$lib/toast.js';
    import {goto} from '$app/navigation';

    const user = getContext('user');
    $: user;

    const modalStore = getModalStore();
    const toastStore = getToastStore();

    /** @type {import('./$types/').ActionData} */
    export let form;

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
    const handleResult = (result) => {
        if (result.type === 'failure') {
            showToastError(toastStore, result.data);
        } else if (result.type === 'success') {
            showToastSuccess(toastStore, 'Successfully updated profile.')
        }
    }

    const showNoChange = () => {
        showToastInfo(toastStore, 'There are no changes to be applied.')
    }

</script>

<svelte:head>
    <title>LibreFit - Profile</title>
</svelte:head>

{#if $user}
<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1>Profile</h1>
        <p>Change your user settings.</p>

        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" use:enhance={({formData, cancel}) => {
            if (formData.get('avatar') === $user.avatar && formData.get('username') === $user.name) {
                cancel();
                showNoChange();
            }

			return async ({ result, update }) => {
                handleResult(result)
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

                <div class="flex flex-row gap-4">
                    <div>
                        <Avatar src={selectedAvatar} initials="LU"/>
                    </div>

                    <div class="justify-center self-center">
                        <button on:click|preventDefault={showAvatarPickerModal}
                                class="btn variant-filled-secondary">Change</button>
                    </div>
                    <input bind:value={selectedAvatar} name="avatar" type="text" style="visibility:hidden"/>
                </div>

            </div>

            {#if !form?.errors}
                <ValidatedInput
                        name="currentPassword"
                        type="password"
                        placeholder="Enter Password"
                        label="Current Password"
                        required
                />
            {:else}
                <ValidatedInput
                        name="currentPassword"
                        type="password"
                        placeholder="Enter Password"
                        label="Current password"
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
{/if}