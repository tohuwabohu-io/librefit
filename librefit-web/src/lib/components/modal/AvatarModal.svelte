<script>
    import AvatarPicker from '$lib/components/AvatarPicker.svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';

    const modalStore = getModalStore();

    const files = [];
    const fileList = ['buffdude-1.png', 'buffdude-2.png', 'lady-1.png', 'lady-2.png', 'panda-1.png', 'tiger-1.png'];

    fileList.forEach(fileName => {
        files.push(`/assets/images/avatars/${fileName}`);
    });

    let selected;

    const onSubmit = (unset) => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                avatar: unset === true ? null : selected
            });
        }
    }

    const onCancel = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                cancelled: true
            });
        }
    }
</script>

<div class="modal block bg-surface-100-800-token w-modal h-auto p-4 space-y-4 rounded-container-token shadow-xl">
    <header class="text-2xl font-bold">
        Choose avatar
    </header>

    <AvatarPicker on:chooseAvatar={(e) => selected = e.detail.avatar} />

    <footer class="modal-footer flex justify-between space-x-2">
        <div>
            <button on:click|preventDefault={() => onSubmit(true)} class="btn variant-ringed">
                Unset
            </button>
        </div>

        <div>
            <button on:click|preventDefault={onCancel} class="btn variant-ringed">
                Cancel
            </button>

            <button on:click|preventDefault={onSubmit} class="btn variant-filled">
                Submit
            </button>
        </div>
    </footer>
</div>