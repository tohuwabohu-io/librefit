<script>
    /** @type {import('./$types').ActionData} */ export let form;
</script>

<svelte:head>
    <title>LibreFit - Registration</title>
</svelte:head>

<section>
    <div class="container mx-auto p-8 space-y-10">
        <h1 class="font-logo">
            <span class="text-primary-500">Sign</span>
            <span class="text-secondary-500">Up</span>
        </h1>

        <form method="POST" class="variant-ringed p-4 space-y-4 rounded-container-token">
            <label class="label">
                <span>E-Mail</span>
                <input name="email" class="input" type="email" placeholder="Enter E-Mail" required/>
            </label>

            <label class="label">
                <span>Password</span>
                <input name="password" class="input" type="password" placeholder="Enter Password" required/>
            </label>

            <label>
                <span>Nickname</span>
                <input name="username" class="input" type="text" placeholder="Enter Nickname (optional)"/>
            </label>

            <label>
                <span>I agree to LibreFit's terms and conditions.</span>
                <input class="checkbox" type="checkbox" name="confirmation"/>
            </label>

            <div>
                <button class="btn variant-filled-primary">Register</button>
            </div>
        </form>
    </div>
    {#if form && !form.success}
        <p>An error occured during registration.</p>
    {/if}
</section>

