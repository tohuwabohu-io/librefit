<script>
    import {createEventDispatcher} from 'svelte';
    import {getDateAsStr, parseStringAsDate} from '$lib/date.js';
    import {subDays} from 'date-fns';

    const dispatch = createEventDispatcher();

    const today = new Date();

    let toDateStr = getDateAsStr(today);
    let fromDateStr = getDateAsStr(subDays(today, 6));

    let filterSelection = 'w';
    const filterOptions = [
        {value: 'w', label: 'last 7 days'},
        {value: 'm', label: 'last 31 days'},
        {value: 'c', label: 'date between'}
    ]

    const onFilter = () => {
        if (filterSelection === 'w') {
            fromDateStr = getDateAsStr(subDays(today, 6));
            toDateStr = getDateAsStr(today);
        } else if (filterSelection === 'm') {
            fromDateStr = getDateAsStr(subDays(today, 30));
            toDateStr = getDateAsStr(today);
        }

        if (filterSelection !== 'c') {
            dispatch('change', {
                from: parseStringAsDate(fromDateStr),
                to: parseStringAsDate(toDateStr)
            });
        }
    }

    const onDateChanged = () => {
        const fromDate = parseStringAsDate(fromDateStr);
        const toDate = parseStringAsDate(toDateStr);

        // can't swap without triggering another change event
        if (fromDate > toDate) {
            dispatch('change', {
                from: toDate,
                to: fromDate
            });
        } else {
            dispatch('change', {
                from: fromDate,
                to: toDate
            });
        }
    }
</script>

<div class="flex md:flex-row flex-col gap-4 items-center">
    <div class="flex flex-row gap-4 items-center">
        <p>
            Show
        </p>
        <div>
            <select class="select" bind:value={filterSelection} on:change={onFilter}>
                {#each filterOptions as filterOption}
                    <option value={filterOption.value}>{filterOption.label}</option>
                {/each}
            </select>
        </div>
    </div>
    <div class="flex flex-row gap-4 { filterSelection !== 'c' ? 'hidden' : '' }">
        <label class="flex flex-row gap-4 items-center">
            <span class="hidden" aria-hidden="true">
                from
            </span>

            <input bind:value={fromDateStr} class="input" type="date" on:change={onDateChanged} />
        </label>

        <label class="flex flex-row gap-4 items-center">
            <span>
                and
            </span>

            <input bind:value={toDateStr} class="input" type="date" on:change={onDateChanged}/>
        </label>
    </div>
</div>