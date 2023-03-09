<script lang="ts">
    export let value, name;
    export let label = 'Input';
    export let type = 'text';
    export let styling = 'input';
    export let placeholder;
    export let validateDetail: () => { valid: boolean, errorMessage?: string };
    export let emptyMessage = `${label} is empty.`
    export let required;

    let error;
    let errorMessage = emptyMessage;

    const getType = (node) => {
        node.type = type;
    }

    export const validate = () => {
        let valid = false;

        if (isEmpty() && required) {
            errorMessage = emptyMessage;
        } else if (validateDetail) {
            const validationResult = validateDetail();

            valid = validationResult.valid;
            errorMessage = validationResult.errorMessage;
        } else {
            valid = true;
        }

        if (!valid) {
            error.classList.remove('invisible');
        } else {
            error.classList.add('invisible');
        }

        return valid;
    }

    const isEmpty = () => {
        return !value || value.length <= 0;
    }

    $:value;
    $:label;
    $:type;
    $:validateDetail;
</script>

<svelte:options accessors={true}/>

<label class="label">
    <span>
        {label}
    </span>
    <input {name} class={styling} use:getType {placeholder} {required}
           bind:value={value} on:focusout={validate} />
    <span bind:this={error} class="text-sm invisible validation-error-text">
        {errorMessage}
    </span>
</label>