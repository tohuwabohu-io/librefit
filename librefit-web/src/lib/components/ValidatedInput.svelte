<script>
    export let value, name;
    export let label = 'Input';
    export let type = 'text';
    export let placeholder;
    export let customValidate = () => true
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
        } else if (!customValidate()) {
            errorMessage = customErrorText;
        } else {
            valid = true;
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