<script>
    import {Avatar} from '@skeletonlabs/skeleton';
    import {createEventDispatcher, getContext} from 'svelte';

    const dispatch = createEventDispatcher();

    const files = [];
    export let fileList = ['buffdude-1.png', 'buffdude-2.png', 'cat-1.png', 'dog-1.png', 'lady-1.png', 'lady-2.png', 'skull-1.png', 'panda-1.png', 'tiger-1.png'];

    fileList.forEach(fileName => {
        files.push(`/assets/images/avatars/${fileName}`);
    });

    export let chosen = 'dog-1.png';

    const avatarClicked = (file) => {
        chosen = file;

        dispatch('chooseAvatar', {
            avatar: file
        });
    }
</script>

<div class="lg:flex lg:flex-row lg:gap-2 grid grid-cols-3 justify-items-center">
    {#each files as file}
        <button on:click={() => avatarClicked(file)} aria-label="choose">
            <Avatar src={file}
                    border="border-4 border-surface-300-600-token hover:!border-primary-500 {file === chosen ? '!border-primary-500' : ''}"
                    cursor="cursor-pointer"/>
        </button>
    {/each}
</div>