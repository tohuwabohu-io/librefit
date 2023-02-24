<script>
    import {Button, Checkbox, Container, TextInput, Title} from "@svelteuidev/core";
</script>

<svelte:head>
    <title>LibreFit - Registration</title>
</svelte:head>

<Container>
    <Title ordering={1}>Registration</Title>

    <form method="POST">
        <TextInput label="Nickname" name="nickname">

        </TextInput>

        <TextInput label="E-Mail" name="username">

        </TextInput>

        <TextInput label="Password" type="password" name="password">

        </TextInput>

        <Checkbox label="I agree to LibreFit's terms and conditions." />

        <Button>
            Register
        </Button>
    </form>
</Container>
