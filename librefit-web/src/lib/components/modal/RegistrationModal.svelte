<script>
    import {getModalStore} from '@skeletonlabs/skeleton';
    import RegistrationComponent from '$lib/components/RegistrationComponent.svelte';

    const modalStore = getModalStore();

    let registerForm;

    if ($modalStore[0] && $modalStore[0].meta && $modalStore[0].meta.form) {
        registerForm = $modalStore[0].meta.form;
    }

    const onCancel = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response(undefined);
        }
    }
</script>

<div class="modal block bg-surface-100-800-token w-modal h-auto p-4 space-y-4 rounded-container-token shadow-xl">
    <RegistrationComponent form={registerForm} isModal="true" onCancel={onCancel}/>
</div>