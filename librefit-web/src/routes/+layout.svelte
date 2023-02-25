<script>
	import {
		Anchor,
		AppShell,
		Header,
		Navbar,
		ShellSection,
		SvelteUIProvider
	} from '@svelteuidev/core';

	let isDark = false;
	let opened = false;

	function toggleTheme() {
		isDark = !isDark;
	}
	function toggleOpened() {
		opened = !opened;
	}
</script>

<SvelteUIProvider withNormalizeCSS withGlobalStyling>
	<AppShell>
		<Header slot="header">
			<Anchor href="/">Home</Anchor>
			<Anchor href="/login">Login</Anchor>
			<Anchor href="/register">Registration</Anchor>
			<Anchor href="/wizard">Wizard</Anchor>
			<Anchor href="/dashboard">Dashboard</Anchor>
		</Header>

		<ShellSection>
			<slot />
		</ShellSection>
	</AppShell>
</SvelteUIProvider>
