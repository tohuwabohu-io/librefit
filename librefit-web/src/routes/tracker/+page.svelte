<script type="ts">
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import TrackerInput from '$lib/components/TrackerInput.svelte';
    import {CalorieTrackerEntry} from 'librefit-api/rest';
    import {Accordion, AccordionItem} from '@skeletonlabs/skeleton';

    let today = new Date();
    let notToday = new Date(2023, 2, 1);
    let todayStr = today.toDateString();

    let trackerMap: Map<String, Array<CalorieTrackerEntry>> = new Map();
    let progressMap: Map<String, number> = new Map();

    let trackerInputs: Array<CalorieTrackerEntry> = [
        {
            added: today,
            amount: 300,
            description: 'b',
            updated: today,
            userId: 1,
            id: 1
        },
        {
            added: today,
            amount: 500,
            description: 'l',
            updated: today,
            userId: 1,
            id: 1
        },
        {
            added: today,
            amount: 400,
            description: 'd',
            updated: today,
            userId: 1,
            id: 1
        }
    ];

    const addEntry = (e) => {
        const newEntry: CalorieTrackerEntry = {
            userId: 1, id: 2, updated: today, added: today, amount: e.detail.value, description: e.detail.category
        }

        trackerMap.get(e.detail.dateStr).push(newEntry);
        calculateProgress(e.detail.dateStr);

        trackerMap = trackerMap;
    }

    const editEntry = (dateStr: String) => {

    }

    const removeEntry = (dateStr: String) => {

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
            description: 'Lunch',
            updated: notToday,
            userId: 1,
            id: 1
        }
    ]);

    for (let key of trackerMap.keys()) {
        calculateProgress(key)
    }

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
                                <TrackerInput category={'s'} value=""
                                              dateStr={trackerKey}
                                              on:add={addEntry}/>
                                {#each trackerMap.get(trackerKey) as trackerInput}
                                    <TrackerInput disabled={true}
                                                  value={trackerInput.amount}
                                                  category={trackerInput.description}
                                                  dateStr={trackerKey}
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
