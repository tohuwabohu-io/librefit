<script>
    import FilterComponent from '$lib/components/FilterComponent.svelte';
    import {getContext} from 'svelte';
    import {listWeightForDate, listWeightRange} from '$lib/api/tracker.js';
    import {showToastError} from '$lib/toast.js';
    import {goto} from '$app/navigation';
    import {Paginator} from '@skeletonlabs/skeleton';
    import ScaleOff from '$lib/assets/icons/scale-outline-off.svg';

    const indicator = getContext('indicator');
    const user = getContext('user');

    if (!$user) goto('/');

    export let data;

    let wtList = [];
    let paginatedSource = [];

    $: wtList;

    let paginationSettings = {
        page: 0,
        limit: 7,
        size: data.wtList.length,
        amounts: [1, 7, 14, 31],
    }

    $: if (data) {
        wtList = data.wtList;
    }

    $: paginatedSource = wtList.slice(
        paginationSettings.page * paginationSettings.limit,
        paginationSettings.page * paginationSettings.limit + paginationSettings.limit
    );

    const onFilterChanged = async (event) => {
        const fromDate = event.detail.from;
        const toDate = event.detail.to;

        console.log(event);

        if (fromDate && toDate) {
            $indicator = $indicator.start();

            await listWeightRange(fromDate, toDate).then(async response => {
                if (response.ok) {
                    wtList = await response.json();
                    paginationSettings.size = wtList.length;
                } else throw response
            }).catch((e) => { showToastError(toastStore, e) }).finally(() => {$indicator = $indicator.finish()})
        }
    }

    /**
     * @param added {string}
     */
    const loadEntries = async (added) => {
        if (!datesToEntries[added]) {
            $indicator = $indicator.start();

            await listWeightForDate(parseStringAsDate(added)).then(async response => {
                datesToEntries[added] = await response;
            }).catch((e) => { showToastError(toastStore, e) }).finally(() => {$indicator = $indicator.finish()})
        }
    }
</script>

<svelte:head>
    <title>LibreFit - Weight Tracker</title>
</svelte:head>

{#if $user}
<section>
    <div class="container mx-auto p-8 space-y-10">
        <h1>History</h1>

        {#if data.wtList}
            {#if wtList.length > 0}
                <div class=" overflow-x-auto space-y-2">
                    <header class="flex justify-between gap-4">
                        <FilterComponent on:change={onFilterChanged} />
                    </header>
                    <table class="table table-hover table-compact table-auto w-full ">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                        {#each paginatedSource as entry}
                            <tr>
                                <td>{entry.added}</td>
                                <td>{entry.amount}</td>
                            </tr>
                        {/each}
                        </tbody>
                    </table>
                    <footer>
                        <!-- <RowCount {handler} /> -->
                        <Paginator
                                bind:settings={paginationSettings}
                                showFirstLastButtons={false}
                                showPreviousNextButtons={true}
                        />
                    </footer>
                </div>
            {:else}
                <div class="flex flex-col items-center text-center gap-4">
                    <ScaleOff width={100} height={100}/>
                    <p>
                        Insufficient data to render your history. Start tracking now on the <a href="/dashboard">Dashboard</a>!
                    </p>
                    <p>
                        Are you trying to add tracking data for the past? Don't worry, the <a href="/import">CSV Import</a>
                        is the right tool for that.
                    </p>
                </div>
            {/if}
        {:else}
            {data.error}
        {/if}
    </div>
</section>
{/if}