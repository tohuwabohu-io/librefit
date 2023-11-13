<script>
    import {page} from '$app/stores';
    import ErrorBot from '$lib/assets/icons/error-bot.svg?component';
</script>

<section>
    <div class="container mx-auto p-8 space-y-8">
        <h1>{$page.status}: {$page.error.message}</h1>
        <p>Whoops. Seems like that took a wrong turn.</p>
        <div class="flex justify-center">
            <ErrorBot height="60vh"/>
        </div>
    </div>
</section>