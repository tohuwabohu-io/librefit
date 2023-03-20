<script type="ts">
    import {CalorieTrackerEntry, CalorieTrackerResourceApi, Configuration} from 'librefit-api/rest'
    import {Category} from 'librefit-api/rest/models';
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';
    import {onMount} from "svelte";
    import {PUBLIC_API_BASE_PATH} from "$env/static/public";

    const api = new CalorieTrackerResourceApi(new Configuration({
        basePath: PUBLIC_API_BASE_PATH
    }));

    let today = new Date();
    let notToday = new Date(2023, 2, 1);
    let todayStr = today.toDateString();

    let trackerMap: Map<String, Array<CalorieTrackerEntry>> = new Map();
    let progressMap: Map<String, number> = new Map();

    const categories = [
        { label: 'Choose...', value: Category.Unset },
        { label: 'Breakfast', value: Category.Breakfast },
        { label: 'Lunch', value: Category.Lunch },
        { label: 'Dinner', value: Category.Dinner },
        { label: 'Snack', value: Category.Snack }
    ];

    let trackerInputs: Array<CalorieTrackerEntry> = [
        {
            added: today,
            amount: 300,
            category: Category.Breakfast,
            updated: today,
            userId: 1,
            id: 1
        },
        {
            added: today,
            amount: 500,
            category: Category.Lunch,
            updated: today,
            userId: 1,
            id: 2
        },
        {
            added: today,
            amount: 400,
            category: Category.Dinner,
            updated: today,
            userId: 1,
            id: 3
        }
    ];

    const addEntry = (e) => {
        const newEntry: CalorieTrackerEntry = {
            userId: 1, id: 2, added: new Date(), amount: e.detail.value, category: e.detail.category
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

        progressMap = progressMap;
    }

    trackerMap.set(todayStr, trackerInputs);
    trackerMap.set(notToday.toDateString(), [
        {
            added: notToday,
            amount: 1400,
            category: Category.Lunch,
            updated: notToday,
            userId: 1,
            id: 1
        }
    ]);

    for (let key of trackerMap.keys()) {
        calculateProgress(key)
    }

    onMount(async () => {
        await api.trackerCaloriesListUserIdGet({
            userId: 1
        })
    })

</script>

<section>
    <div class="container mx-auto p-8 space-y-10">
        <Accordion>
            {#each [...trackerMap.keys()] as trackerKey}
                <AccordionItem open={todayStr === trackerKey}>
                    <svelte:fragment slot="summary">{trackerKey}</svelte:fragment>
                    <svelte:fragment slot="content">
                        <div class="flex gap-4 justify-between">
                            <TrackerRadial current={progressMap.get(trackerKey)}/>

                            <div class="flex flex-col grow gap-4">
                                <TrackerInput categories={categories} value=""
                                              dateStr={trackerKey}
                                              id={-1}
                                              on:add={addEntry}/>
                                {#each trackerMap.get(trackerKey) as trackerInput}
                                    <TrackerInput disabled={true}
                                                  value={trackerInput.amount}
                                                  categories={categories}
                                                  category={trackerInput.category}
                                                  dateStr={trackerKey}
                                                  id={trackerInput.id}
                                                  on:add={addEntry}
                                                  on:edit={editEntry}
                                                  on:remove={removeEntry}/>
                                {/each}
                            </div>
                        </div>
                    </svelte:fragment>
                </AccordionItem>
            {/each}
        </Accordion>
    </div>
</section>
