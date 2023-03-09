<script lang="ts">
    export let value, name;
    export let label = 'Input';
    export let type = 'text';
    export let placeholder;
    export let customValidate: () => { valid: boolean, errorMessage?: string } = () => { return { valid: true }}
    export let customErrorText = '';
    export let emptyMessage = `${label} is empty.`
    export let required;

    let error;
    let errorMessage = emptyMessage;

    const getType = (node) => {
        node.type = type;
    }

    export const validate = () => {
        let valid = false;

        if (isEmpty()) {
            errorMessage = emptyMessage;
        } else if (customValidate) {
            const validationResult = customValidate();

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
    $:customValidate
    $:customErrorText;
</script>

<label class="label">
    <span>{label}</span>
    <input {name} class="input" use:getType {placeholder} {required}
           bind:value={value} on:focusout={validate} />
    <span bind:this={error} class="text-sm invisible validation-error-text">{errorMessage}</span>
</label>