<script>
	import { Button, Checkbox, Container, Text, TextInput, Title } from '@svelteuidev/core';

	/** @type {import('./$types').ActionData} */ export let form;
</script>

<svelte:head>
	<title>LibreFit - Registration</title>
</svelte:head>

<Container>
	<Title ordering={1}>Registration</Title>

	<form method="POST">
		<TextInput label="Nickname" name="username" />

		<TextInput label="E-Mail" name="email" required />

		<TextInput label="Password" type="password" name="password" required />

		<Checkbox label="I agree to LibreFit's terms and conditions." />

		<Button>Register</Button>
	</form>

	{#if form && !form.success}
		<Text>An error occured during registration.</Text>
	{/if}
</Container>
