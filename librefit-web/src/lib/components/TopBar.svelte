<script>
import {AppBar} from "@skeletonlabs/skeleton";
</script>

<AppBar>
    <svelte:fragment slot="lead">
        <a href="/" class="font-logo">
            <span class="variant-filled-primary">
                Libre
            </span>
            <span class="variant-filled-secondary">
                Fit
            </span>
        </a>
    </svelte:fragment>
    <svelte:fragment slot="trail">
        <a href="/login">Login</a>
        <a href="/register">Registration</a>
        <a href="/wizard">Wizard</a>
        <a href="/dashboard">Dashboard</a>
    </svelte:fragment>
</AppBar>