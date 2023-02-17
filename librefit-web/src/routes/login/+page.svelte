<svelte:head>
    <title>LibreFit - Login</title>
</svelte:head>

<script>
    /** @type {import('./$types').PageData} */  export let data;
    /** @type {import('./$types').ActionData} */  export let form;
</script>

<style>
    .login-page {
        display: flex;
        flex-direction: column;
    }
</style>

<div class="login-page">

    <h1>Login</h1>
    <p>Hallo!</p>

    <form method="POST">
        <div class="input-form">
            <label for="username">Username</label>
            <input id="username" name="username" type="text">
            <label for="password">Password</label>
            <input id="password" name="password" type="password">

            <div>
                <button>Login</button>
            </div>
        </div>
    </form>


    <div>
        {#if form?.success}
            <p>Login successful! Welcome!</p>
        {/if}

        {#if !form?.success}
            <p>Error during login.</p>
        {/if}
    </div>

</div>
