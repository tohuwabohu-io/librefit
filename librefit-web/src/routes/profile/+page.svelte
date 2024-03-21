<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {getContext} from 'svelte';
    import {Avatar, getModalStore, getToastStore} from '@skeletonlabs/skeleton';
    import {showToastError, showToastInfo, showToastSuccess, showToastWarning} from '$lib/toast.js';
    import {updateProfile} from '$lib/api/user.js';
    import {getFieldError} from '$lib/validation.js';
    import {goto} from '$app/navigation';

    const user = getContext('user');
    if (!$user) goto('/');

    $: user;

    const indicator = getContext('indicator');

    const modalStore = getModalStore();
    const toastStore = getToastStore();

    let btnSubmit;

    let selectedAvatar = $user.avatar;

    let status;

    const handleSubmit = async (event) => {
        const formData = new FormData(event.currentTarget);

        if (formData.get('avatar') === $user.avatar && formData.get('username') === $user.name) {
            showToastInfo(toastStore, 'There are no changes to be applied.')
        } else {
            $indicator = $indicator.start(btnSubmit);

            await updateProfile(formData).then(async response => {
                if (response.ok) {
                    showToastSuccess(toastStore, 'Successfully updated profile.');

                    user.set(await response.json());
                } else throw response
            }).catch(error => {
                if (!error.data.error) {
                    showToastWarning(toastStore, 'Please check your input.')
                } else {
                    showToastError(toastStore, error);
                }

                status = error.data;
            }).finally(() => $indicator = $indicator.finish());
        }
    }

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

<svelte:head>
    <title>LibreFit - Profile</title>
</svelte:head>

{#if $user}
<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1>Profile</h1>
        <p>Change your user settings.</p>

        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" on:submit|preventDefault={handleSubmit}>
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

            <ValidatedInput
                    name="currentPassword"
                    type="password"
                    placeholder="Enter Password"
                    label="Current password"
                    required
                    errorMessage={getFieldError(status, 'password')}
            />

            <div class="flex justify-between">
                <button bind:this={btnSubmit} class="btn variant-filled-primary">Update</button>
            </div>
        </form>
    </div>
</section>
{/if}