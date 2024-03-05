<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import { enhance } from '$app/forms';
    import {getFieldError} from '$lib/validation.js';
    import {showToastError, showToastSuccess} from '$lib/toast.js';
    import {FileDropzone, getToastStore, RadioGroup, RadioItem} from '@skeletonlabs/skeleton';
    import FileUpload from '$lib/assets/icons/file-upload.svg?component';

    const toastStore = getToastStore();

    /** @type {import('./$types/').ActionData} */
    export let form;

    /** @type Array */
    const radioOptions = [
        { label: 'All', value: 'A' },
        { label: 'Calories only', value : 'C' },
        { label: 'Weight only', value: 'W' }
    ];

    let importGroup = 'A';

    const handleResult = (result) => {
        if (result.success) {
            showToastSuccess(toastStore, 'Import successful.');
        } else {
            showToastError(toastStore, result?.data);
        }
    }
</script>

<svelte:head>
    <title>LibreFit - CSV Import</title>
</svelte:head>

<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1>Import</h1>
        <h2>Upload data from existing sources.</h2>

        <form class="variant-ringed p-4 space-y-4 rounded-container-token" method="POST" enctype="multipart/form-data"
              action="?/startImport" use:enhance={({formData, cancel}) => {
            return async ({ result, update }) => {
                update({ reset: false });

                handleResult(result);
            };}}
        >

            <ValidatedInput
                    name="datePattern"
                    type="text"
                    placeholder="d-MMM-yyyy"
                    value="d-MMM-yyyy"
                    label="Date format"
                    required
                    errorMessage={getFieldError($form?.status, 'datePattern')}
            />

            <ValidatedInput
                    name="headerLength"
                    type="text"
                    placeholder="2"
                    value="2"
                    label="No. of header rows"
                    required
                    errorMessage={getFieldError($form?.status, 'headerLength')}
            />

            <p>
                Take data for
            </p>
            <RadioGroup>
                {#each radioOptions as option}
                    <RadioItem value={option.value} name="types" bind:group={importGroup}>
                        {option.label}
                    </RadioItem>
                {/each}
            </RadioGroup>

            <ValidatedInput
                    name="drop"
                    type="checkbox"
                    styling="checkbox self-center"
            >
                Overwrite duplicates
            </ValidatedInput>

            <FileDropzone name="file">
                <svelte:fragment slot="lead">
                    <div class="btn-icon scale-150">
                        <FileUpload/>
                    </div>
                </svelte:fragment>
                <svelte:fragment slot="meta">CSV allowed.</svelte:fragment>
            </FileDropzone>

            <div class="flex justify-between">
                <button class="btn variant-filled-primary">Import</button>
            </div>
        </form>
    </div>
</section>