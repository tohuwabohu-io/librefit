<script type="ts">
    import {Configuration} from 'librefit-api/rest';
    import {CalorieTrackerResourceApi} from 'librefit-api/rest';
    import {CalorieTrackerEntry } from 'librefit-api/rest';
    import {Category} from 'librefit-api/rest';
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';
    import {onMount} from "svelte";
    import {PUBLIC_API_BASE_PATH} from "$env/static/public";
    import format from 'date-fns/format';

    const api = new CalorieTrackerResourceApi(new Configuration({
        basePath: PUBLIC_API_BASE_PATH
    }));

    let today = new Date();
    let todayStr = format(today, 'yyyy-MM-dd');

    const availableDates = new Array<String>();
    availableDates.push(todayStr);

    $: trackerMap = new Map<String, Array<CalorieTrackerEntry>>();
    let progressMap: Map<String, number> = new Map();

    const categories = Object.keys(Category).map(key => {
        return {
            label: key,
            value: Category[key]
        }
    })

    const addEntry = (e) => {
        const newEntry: CalorieTrackerEntry = {
            userId: 1,
            added: todayStr,
            amount: e.detail.value,
            category: e.detail.category
        }

        api.trackerCaloriesCreatePost({
            calorieTrackerEntry: newEntry
        }).then((result) => {
            trackerMap.get(e.detail.dateStr).push(newEntry);
            calculateProgress(e.detail.dateStr);
        }).catch(console.error)
    }

    const editEntry = (e) => {

    }

    const removeEntry = (e) => {

    }

    const calculateProgress = (dateStr: String) => {
        const progress = trackerMap.get(dateStr).map((entry) => entry.amount).reduce((part, a) => part + a);

        progressMap.set(dateStr, progress);
    }

    const calc = (dateStr: String): number => {
        return trackerMap.get(dateStr)?.map((entry) => entry.amount).reduce((a, b) => a + b);
    }

    const loadEntriesForDate = async (dateStr: String) => {
        if (!trackerMap.has(dateStr)) {
            await api.trackerCaloriesListUserIdDateGet({
                userId: 1,
                date: dateStr
            }).then((entries: Array<CalorieTrackerEntry>) => {
                for (let key of trackerMap.keys()) {
                    calculateProgress(key)
                }

                if (!progressMap.has(todayStr)) {
                    progressMap.set(todayStr, 0);
                }

                progressMap = progressMap

                trackerMap.set(dateStr, entries);
            }).catch((e) => console.error(e));
        }
    }

    onMount(async () => {
        await api.trackerCaloriesListUserIdDatesGet({
            userId: 1
        }).then((dates: Array<String>) => {
            for (let date of dates) {
                loadEntriesForDate(date)
            }

            availableDates.push(...dates)
        }).catch((e) => console.error(e));
    })
</script>

<section>
    <div class="container mx-auto p-8 space-y-10">
        <Accordion>
            {#each availableDates as date}
                <AccordionItem id={date} open={todayStr === date}>
                    <svelte:fragment slot="summary">{date}</svelte:fragment>
                        <svelte:fragment slot="content">
                            <div class="flex gap-4 justify-between">
                                <TrackerRadial current={calc(date)} />

                                <div class="flex flex-col grow gap-4">
                                    <TrackerInput categories={categories} value=""
                                                  dateStr={date}
                                                  id={-1}
                                                  on:add={addEntry}/>
                                    {#if trackerMap.has(date)}
                                        {#each trackerMap.get(date) as trackerInput}
                                            <TrackerInput disabled={true}
                                                          value={trackerInput.amount}
                                                          categories={categories}
                                                          category={trackerInput.category}
                                                          dateStr={date}
                                                          id={trackerInput.id}
                                                          on:add={addEntry}
                                                          on:edit={editEntry}
                                                          on:remove={removeEntry}/>
                                        {/each}
                                    {/if}
                                </div>
                            </div>
                        </svelte:fragment>
                </AccordionItem>
            {/each}
        </Accordion>
    </div>
</section>
