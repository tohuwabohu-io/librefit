<script>
    import CalorieDistribution from '$lib/components/CalorieDistribution.svelte';
    import {getContext} from 'svelte';
    import {goto} from '$app/navigation';
    import {DataViews, enumKeys} from '$lib/enum.js';
    import {getToastStore, RadioGroup, RadioItem} from '@skeletonlabs/skeleton';
    import {listCaloriesFiltered} from '$lib/api/tracker.js';
    import {showToastError} from '$lib/toast.js';
    import NoFood from '$lib/assets/icons/food-off.svg?component'
    import {getFoodCategoryLongvalue, skimCategories} from '$lib/api/category.js';

    const toastStore = getToastStore();

    const user = getContext('user');
    const indicator = getContext('indicator');
    const ctList = getContext('ctList');
    const foodCategories = getContext('foodCategories');

    if (!$user) goto('/');

    let filter = DataViews.Month;
    let filteredData = $ctList;

    let categories = skimCategories($ctList);

    $: filteredData;
    $: categories;

    const loadEntriesFiltered = async () => {
        $indicator = $indicator.start();

        await listCaloriesFiltered(filter).then(async (result) => {
            /** @type Array<WeightTrackerEntry> */
            filteredData = await result.json();
        }).catch(e => showToastError(toastStore, e)).finally(() => $indicator = $indicator.finish());
    }

    /**
     * @param calorieTrackerEntries {Array<CalorieTrackerEntry>}
     * @param category {string}
     */
    const calculateAverage = (calorieTrackerEntries, category) => {
        const filtered = calorieTrackerEntries.filter(entry => entry.category === category)
        const total = filtered.map(entry => entry.amount).reduce((part, a) => part + a, 0);

        return Math.round(total / filtered.length);
    }

    /**
     * @param calorieTrackerEntries {Array<CalorieTrackerEntry>}
     * @param category {string}
     */
    const findMinimum = (calorieTrackerEntries, category) => {
        return Math.min(...calorieTrackerEntries.filter(entry => entry.category === category).map(entry => entry.amount));
    }

    /**
     * @param calorieTrackerEntries {Array<CalorieTrackerEntry>}
     * @param category {string}
     */
    const findMaximum = (calorieTrackerEntries, category) => {
        return Math.max(...calorieTrackerEntries.filter(entry => entry.category === category).map(entry => entry.amount))
    }

</script>

{#if $user}
<section>
    <div class="container mx-auto p-8 space-y-10">
        <h1 class="h1">
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

            {#if $ctList.length > 0 }
                <div class="flex flex-col lg:flex-row gap-2">
                    <CalorieDistribution displayClass="lg:w-1/4" ctList={filteredData} displayHeader={false} displayHistory={false}/>

                    <div class="lg:w-3/5 w-full flex flex-col grow align-middle self-center">
                        <h2 class="h2">Last {filter.toLowerCase()}:</h2>
                        <div class="table-container">
                            <table class="table table-hover table-compact table-auto w-full align-middle">
                                <thead>
                                    <tr>
                                        <th>Type</th>
                                        <th>Avg.</th>
                                        <th>Min.</th>
                                        <th>Max.</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {#each categories as category}
                                        <tr>
                                            <td>
                                                {getFoodCategoryLongvalue($foodCategories, category)}
                                            </td>
                                            <td>
                                                kcal {calculateAverage(filteredData, category)}
                                            </td>
                                            <td>
                                                kcal {findMinimum(filteredData, category)}
                                            </td>
                                            <td>
                                                kcal {findMaximum(filteredData, category)}
                                            </td>
                                        </tr>
                                    {/each}
                                </tbody>
                            </table>
                        </div>
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