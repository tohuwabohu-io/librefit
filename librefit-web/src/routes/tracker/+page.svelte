<script type="ts">
    import {Configuration, CalorieTrackerResourceApi} from 'librefit-api/rest';
    import {onMount} from "svelte";
    import {PUBLIC_API_BASE_PATH} from "$env/static/public";
    import format from 'date-fns/format';
    import CalorieTracker from "$lib/components/tracker/CalorieTracker.svelte";

    const api = new CalorieTrackerResourceApi(new Configuration({
        basePath: PUBLIC_API_BASE_PATH
    }));

    let today = new Date();
    let todayStr = format(today, 'yyyy-MM-dd');

    let availableDates = new Set<String>();
    availableDates.add(todayStr);

    onMount(async () => {
        await api.trackerCaloriesListUserIdDatesGet({
            userId: 1
        }).then((dates: Array<String>) => {
            dates.forEach(d => availableDates.add(d))
            availableDates = availableDates
        }).catch((e) => console.error(e));
    })
</script>

<section>
    <div class="container mx-auto p-8 space-y-10">
        {#each [...availableDates] as date}
            <CalorieTracker bind:date={date} today={todayStr}/>
        {/each}
    </div>
</section>
