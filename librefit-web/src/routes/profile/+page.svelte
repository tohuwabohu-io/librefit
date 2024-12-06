<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {getContext} from 'svelte';
    import {Avatar, getModalStore, getToastStore} from '@skeletonlabs/skeleton';
    import {showToastError, showToastInfo, showToastSuccess, showToastWarning} from '$lib/toast.js';
    import {updateProfile} from '$lib/api/user.js';
    import {getFieldError} from '$lib/validation.js';
    import {goto} from '$app/navigation';

    const user = getContext('user');
    $: user;

    const indicator = getContext('indicator');

    const modalStore = getModalStore();
    const toastStore = getToastStore();

    let btnSubmit;

    let selectedAvatar = $user.avatar;

    const handleSubmit = async (event) => {
        const formData = new FormData(event.currentTarget);

        if (formData.get('avatar') === $user.avatar && formData.get('username') === $user.name) {
            showToastInfo(toastStore, 'There are no changes to be applied.')
        } else {
            $indicator = $indicator.start(btnSubmit);

            await updateProfile(formData).then(async response => {
                showToastSuccess(toastStore, 'Successfully updated profile.');

                user.set(response);
            }).catch(error => {
                console.log(error);
                
                if (!error.data.error) {
                    showToastWarning(toastStore, 'Please check your input.')
                } else {
                    showToastError(toastStore, error);
                }
            }).finally(() => $indicator = $indicator.finish());
        }
    }

    const showAvatarPickerModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'avatarModal',
            response: (e) => {
                if (e && !e.cancelled) {
                    selectedAvatar = e.avatar;
                }

                modalStore.close();
            }
        });
    }
</script>

<svelte:head>
    <title>LibreFit - Profile</title>
</svelte:head>

{#if $user}
<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1 class="h1">Profile</h1>
        <p>Change your user settings.</p>

        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" on:submit|preventDefault={handleSubmit}>
            <label class="label">
                <span>Nickname</span>
                <input
                        value={$user.name}
                        name="username"
                        class="input"
                        type="text"
                        placeholder="Enter Nickname"
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

            <div class="flex justify-between">
                <button bind:this={btnSubmit} class="btn variant-filled-primary">Update</button>
            </div>
        </form>
    </div>
</section>
{/if}