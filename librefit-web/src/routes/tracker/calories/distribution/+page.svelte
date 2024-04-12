<script>
    import CalorieDistribution from '$lib/components/CalorieDistribution.svelte';
    import {getContext} from 'svelte';
    import {goto} from '$app/navigation';
    import {DataViews, enumKeys} from '$lib/enum.js';
    import {RadioGroup, RadioItem} from '@skeletonlabs/skeleton';
    import {listCaloriesFiltered} from '$lib/api/tracker.js';
    import {showToastError} from '$lib/toast.js';
    import NoFood from '$lib/assets/icons/food-off.svg?component'

    const user = getContext('user');
    const indicator = getContext('indicator');
    const ctList = getContext('ctList');

    if (!$user) goto('/');

    let filter = DataViews.Month;
    let chartData = $ctList;

    $: chartData;

    const loadEntriesFiltered = async () => {
        $indicator = $indicator.start();

        await listCaloriesFiltered(filter).then(async (result) => {
            /** @type Array<WeightTrackerEntry> */
            chartData = await result.json();
        }).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
    }

</script>

{#if $user}
<section>
    <div class="container mx-auto p-8 space-y-10">
        <h1>
            Calorie Distribution
        </h1>
        <div class="flex flex-col gap-4">
            <RadioGroup>
                {#each enumKeys(DataViews) as dataView}
                    <RadioItem bind:group={filter}
                               name="justify"
                               value={DataViews[dataView]}
                               on:change={loadEntriesFiltered}>
                        {dataView}
                    </RadioItem>
                {/each}
            </RadioGroup>

            {#if $ctList }
                <div class="flex grow xl:flex-row">
                    <CalorieDistribution displayClass="grow" ctList={chartData} displayHeader={false}/>

                    <div>
                        <p>Hallo</p>
                    </div>
                </div>
            {:else}
                <div class="flex flex-col items-center text-center gap-4">
                    <NoFood width={100} height={100}/>
                    <p>
                        Insufficient data to render your history.
                    </p>
                </div>
            {/if}
        </div>
    </div>
</section>
{/if}