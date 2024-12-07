<script lang="ts">
	import AvatarPicker from '$lib/components/AvatarPicker.svelte';
	import { getModalStore } from '@skeletonlabs/skeleton';
	import { getContext } from 'svelte';
	import type { LibreUser } from '$lib/model';

	const modalStore = getModalStore();
	const user: LibreUser = getContext('user');

	let selected;

	const onSubmit = (unset) => {
		if ($modalStore[0].response) {
			$modalStore[0].response({
				avatar: unset === true ? null : selected
			});
		}
	};

	const onCancel = () => {
		if ($modalStore[0].response) {
			$modalStore[0].response({
				cancelled: true
			});
		}
	};
</script>

<div class="modal block bg-surface-100-800-token sm-lg:w-modal h-auto p-4 space-y-4 rounded-container-token shadow-xl">
	<header class="text-2xl font-bold">
		Choose avatar
	</header>

	<AvatarPicker chosen={user.avatar} on:chooseAvatar={(e) => selected = e.detail.avatar} />

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