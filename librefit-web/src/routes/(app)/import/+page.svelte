<script>
    import ValidatedInput from '$lib/components/ValidatedInput.svelte';
    import {applyAction, enhance} from '$app/forms';
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

    /** @type FileList */
    let files;

    const handleResult = (result) => {
        if (result.data.success) {
            showToastSuccess(toastStore, 'Import successful.');
        } else {
            showToastError(toastStore, result?.data.errors);
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
              action="?/startImport" use:enhance={() => {
            return async ({ result, update }) => {
                update({ reset: false });

                handleResult(result);

                await applyAction(result);
            };}}
        >

            <ValidatedInput
                    name="datePattern"
                    type="text"
                    placeholder="d-MMM-yyyy"
                    value="d-MMM-yyyy"
                    label="Date format"
                    required
                    errorMessage={getFieldError(form, 'datePattern')}
            />

            <ValidatedInput
                    name="headerLength"
                    type="text"
                    placeholder="2"
                    value="2"
                    label="Number of header rows"
                    required
                    errorMessage={getFieldError(form, 'headerLength')}
            />

            <p>
                Take data for
            </p>
            <RadioGroup>
                {#each radioOptions as option}
                    <RadioItem value={option.value} name="importer" bind:group={importGroup}>
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

            <input value={files ? files[0].name : ''} name="fileName" type="text" hidden aria-hidden="true"/>

            <FileDropzone name="file" bind:files={files} accept="text/csv">
                <svelte:fragment slot="lead">
                    <div class="btn-icon scale-150">
                        <FileUpload/>

                        {#if getFieldError(form, 'file') }
                            <strong class="text-error-400"> {getFieldError(form, 'file')} </strong>
                        {/if}
                    </div>
                </svelte:fragment>
                <svelte:fragment slot="message">
                    {#if files}
                        <p>
                            Selected: {files[0].name}
                        </p>
                    {:else}
                        <strong>Upload a file</strong> or drag and drop
                    {/if}
                </svelte:fragment>
                <svelte:fragment slot="meta">
                    {#if files}
                        Size: {files[0].size} Bytes
                    {:else}
                        CSV allowed. Max. size: 32 KB
                    {/if}
                </svelte:fragment>
            </FileDropzone>

            <div class="flex justify-between">
                <button class="btn variant-filled-primary">Import</button>
            </div>
        </form>
    </div>
</section>