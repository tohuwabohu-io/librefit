<script>
	import {AppBar, Avatar, getDrawerStore} from '@skeletonlabs/skeleton';
	const drawerStore = getDrawerStore();

	export const showDrawer = (e) => {
		drawerStore.open({
			width: 'w-1/3'
		});
	}
</script>

<AppBar shadow="drop-shadow">
	<svelte:fragment slot="lead">
		<a href="/" class="font-logo text-2xl">
			<span class="text-primary-500"> Libre </span>
			<span class="text-secondary-500"> Fit </span>
		</a>
	</svelte:fragment>
	<svelte:fragment slot="trail">
		<Avatar initials="LU" width="w-12"
				border="border-4 border-surface-300-600-token hover:!border-primary-500"
				cursor="cursor-pointer"
				on:click={showDrawer}
		/>
	</svelte:fragment>
</AppBar>
