<svelte:head>
    <title>LibreFit - Registration</title>
</svelte:head>

<div class="registration-page">
    <h1>Register</h1>
    <p>Please fill the registration form.</p>
    <form action="http://localhost:8080/user/register" method="POST">
        <div class="input-form">
            <label for="name">Nickname</label>
            <input id="name" type="text">
            <label for="email">E-Mail</label>
            <input id="email" type="email">
            <label for="password">Password</label>
            <input id="password" type="password">
            <div>
                <button type="submit">Register</button>
            </div>
        </div>
    </form>
</div>

<style>
    .registration-page {
        display: flex;
        flex-direction: column;
    }
</style>