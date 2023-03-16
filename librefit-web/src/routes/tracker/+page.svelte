<script type="ts">
    import TrackingRadial from "$lib/components/TrackingRadial.svelte";
    import TrackerInput from "$lib/components/TrackerInput.svelte";
    import { CalorieTrackerEntry } from 'librefit-api/rest'
    import { Accordion, AccordionItem } from '@skeletonlabs/skeleton';

    let today = new Date();
    let notToday = new Date(2023, 3, 1);
    let todayStr = today.toDateString();
    let trackerMap: Map<String, Array<CalorieTrackerEntry>> = new Map()

    let trackerInputs: Array<CalorieTrackerEntry> = [{
        added: today,
        amount: 1200,
        description: 'Lunch',
        updated: today,
        userId: 1,
        id: 1
    }]

    trackerMap.set(todayStr, trackerInputs)
    trackerMap.set(notToday.toDateString(), [{
        added: notToday,
        amount: 1400,
        description: 'Lunch',
        updated: notToday,
        userId: 1,
        id: 1
    }])
</script>

<section>
    <div class="container mx-auto p-8 space-y-10">
        <Accordion>
            {#each [...trackerMap.keys()] as trackerKey}
                {#each trackerMap.get(trackerKey) as trackerInput}
                    <AccordionItem open={todayStr === trackerKey}>
                        <svelte:fragment slot="summary">{trackerKey}</svelte:fragment>
                        <svelte:fragment slot="content">
                            <div class="flex space-between">
                                <TrackingRadial></TrackingRadial>
                            </div>

                            <div>
                                <TrackerInput label="kcal"></TrackerInput>

                                <button class="variant-filled-primary">
                                    +
                                </button>
                            </div>
                        </svelte:fragment>
                    </AccordionItem>
                {/each}
            {/each}
        </Accordion>
    </div>
</section>