<script type="ts">
    import {Configuration} from 'librefit-api/rest';
    import {CalorieTrackerResourceApi} from 'librefit-api/rest/apis';
    import {CalorieTrackerEntry } from 'librefit-api/rest/models';
    import {Category} from 'librefit-api/rest/models';
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';
    import {onMount} from "svelte";
    import {PUBLIC_API_BASE_PATH} from "$env/static/public";
    import {format} from 'date-fns';

    const api = new CalorieTrackerResourceApi(new Configuration({
        basePath: PUBLIC_API_BASE_PATH
    }));

    let today = new Date();
    let todayStr = format(today, 'yyyy-MM-dd')

    let availableDates: Array<String> = new Array<String>();
    availableDates.push(todayStr);

    let trackerMap: Map<String, Array<CalorieTrackerEntry>> = new Map();
    let progressMap: Map<String, number> = new Map();

    const categories = Object.keys(Category).map(key => {
        return {
            label: key,
            value: Category[key]
        }
    })

    const addEntry = (e) => {
        const newEntry: CalorieTrackerEntry = {
            userId: 1, id: 2, added: todayStr, amount: e.detail.value, category: e.detail.category
        }

        trackerMap.get(e.detail.dateStr).push(newEntry);
        calculateProgress(e.detail.dateStr);

        trackerMap = trackerMap;
    }

    const editEntry = (e) => {

    }

    const removeEntry = (e) => {

    }

    const calculateProgress = (dateStr: String) => {
        const progress = trackerMap.get(dateStr).map((entry) => entry.amount).reduce((part, a) => part + a);

        progressMap.set(dateStr, progress);
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

                progressMap = progressMap

                trackerMap.set(dateStr, entries);
                trackerMap = trackerMap
            }).catch((e) => console.error(e));
        }
    }

    onMount(async () => {
        await api.trackerCaloriesListUserIdDatesGet({
            userId: 1
        }).then((dates: Array<String>) => {
            availableDates.push(...dates)

            for (let date of dates) {
                loadEntriesForDate(date)
            }

            availableDates = availableDates
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
                                <TrackerRadial current={progressMap.get(date)}/>

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
