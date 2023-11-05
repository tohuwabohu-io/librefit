<script>
    import {Avatar} from '@skeletonlabs/skeleton';
    import {createEventDispatcher} from 'svelte';

    const dispatch = createEventDispatcher();

    const files = [];
    const fileList = ['buffdude-1.png', 'buffdude-2.png', 'lady-1.png', 'lady-2.png', 'panda-1.png', 'tiger-1.png'];

    fileList.forEach(fileName => {
        files.push(`/assets/images/avatars/${fileName}`);
    });

    const avatarClicked = (file) => {
        console.log(file);

        dispatch('chooseAvatar', {
            avatar: file
        });
    }
</script>

<div class="flex flex-row gap-4">
    {#each files as file}
        <Avatar src={file} on:click={() => avatarClicked(file)}
                border="border-4 border-surface-300-600-token hover:!border-primary-500"
                cursor="cursor-pointer"/>
    {/each}
</div>