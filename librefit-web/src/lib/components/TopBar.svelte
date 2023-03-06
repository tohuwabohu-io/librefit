<script>
import {AppBar} from "@skeletonlabs/skeleton";
</script>

<AppBar>
    <svelte:fragment slot="lead">Libre Fit</svelte:fragment>
    <svelte:fragment slot="trail">
        <a href="/">Home</a>
        <a href="/login">Login</a>
        <a href="/register">Registration</a>
        <a href="/wizard">Wizard</a>
        <a href="/dashboard">Dashboard</a>
    </svelte:fragment>
</AppBar>