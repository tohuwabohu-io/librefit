<script>
    import {getModalStore} from '@skeletonlabs/skeleton';

    let value;

    const modalStore = getModalStore();

    const onSubmit = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response({
                detail: {
                    value: value
                }
            });
        }
    }

    const onCancel = () => {
        if ($modalStore[0].response) {
            $modalStore[0].response(undefined);
        }
    }
</script>