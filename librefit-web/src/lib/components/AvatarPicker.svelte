<script>
    import {Avatar} from '@skeletonlabs/skeleton';
    import {createEventDispatcher, getContext} from 'svelte';

    const dispatch = createEventDispatcher();
    const user = getContext('user');

    const files = [];
    const fileList = ['buffdude-1.png', 'buffdude-2.png', 'cat-1.png', 'dog-1.png', 'lady-1.png', 'lady-2.png', 'skull-1.png', 'panda-1.png', 'tiger-1.png'];

    fileList.forEach(fileName => {
        files.push(`/assets/images/avatars/${fileName}`);
    });

    let chosen = $user.avatar;

    const avatarClicked = (file) => {
        chosen = file;

        dispatch('chooseAvatar', {
            avatar: file
        });
    }
</script>

<div class="flex flex-row gap-4">
    {#each files as file}
        <Avatar src={file} on:click={() => avatarClicked(file)}
                border="border-4 border-surface-300-600-token hover:!border-primary-500 {file === chosen ? '!border-primary-500' : ''}"
                cursor="cursor-pointer"/>
    {/each}
</div>