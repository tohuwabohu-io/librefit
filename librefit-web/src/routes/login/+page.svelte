<svelte:head>
    <title>LibreFit - Login</title>
</svelte:head>

<div>
    <h1>Login</h1>
    <p>Hallo!</p>
    <div>
        <form action="http://localhost:8080/user/login">
            <label for="username">Username</label>
            <input id="username" type="text">
            <label for="password">Password</label>
            <input id="password" type="password">
            <button type="submit">Login</button>
        </form>
    </div>
</div>