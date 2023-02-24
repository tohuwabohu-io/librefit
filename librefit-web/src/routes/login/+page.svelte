<svelte:head>
    <title>LibreFit - Login</title>
</svelte:head>

<script>
    import {Button, Container, TextInput, Title} from "@svelteuidev/core";

    /** @type {import('./$types').PageData} */  export let data;
    /** @type {import('./$types').ActionData} */  export let form;
</script>

<Container>
    <Title ordering={1}>Login</Title>

    <form method="POST">
        <TextInput label="E-Mail" name="username">

        </TextInput>

        <TextInput label="Password" type="password" name="password">

        </TextInput>

        <Button>
            Login
        </Button>
    </form>
</Container>

{#if form?.success}
    <p>Login successful! Welcome!</p>
{/if}

{#if !form?.success}
    <p>Error during login.</p>
{/if}