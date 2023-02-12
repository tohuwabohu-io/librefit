<svelte:head>
    <title>LibreFit - Registration</title>
</svelte:head>

<div>
    <h1>Register</h1>
    <p>Please fill the registration form.</p>
    <div>
        <form action="http://localhost:8080/user/register" method="POST">
            <label for="name">Nickname</label>
            <input id="name" type="text">
            <label for="email">E-Mail</label>
            <input id="email" type="email">
            <label for="password">Password</label>
            <input id="password" type="password">
            <button type="submit">Register</button>
        </form>
    </div>
</div>