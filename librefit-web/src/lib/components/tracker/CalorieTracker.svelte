<script>
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import {createEventDispatcher, getContext} from 'svelte';
    import {getDateAsStr, getDaytimeFoodCategory} from '$lib/date.js';
    import Plus from '$lib/assets/icons/plus.svg';
    import Edit from '$lib/assets/icons/pencil.svg?component';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import TrackerInput from '$lib/components/TrackerInput.svelte';

    const currentGoal = getContext('currentGoal');
    const modalStore = getModalStore();

    export let entries;

    /** @type Array<FoodCategory> */
    export let categories;

    const dispatch = createEventDispatcher();

    let caloriesQuickAdd;

    const addCaloriesQuickly = (e) => {
        // take default category based on time
        const now = new Date();

        dispatch('addCalories', {
            dateStr: getDateAsStr(now),
            value: caloriesQuickAdd,
            category: getDaytimeFoodCategory(now),
            target: e.detail.target,
            callback: () => {
                e.detail.callback();
                caloriesQuickAdd = undefined;
            }
        });
    }

    const addCalories = (e) => {
        dispatch('addCalories', {
            dateStr: e.detail.dateStr,
            value: e.detail.value,
            category: e.detail.category,
            target: e.detail.target,
            callback: e.detail.callback
        });
    }

    const updateCalories = (e) => {
        dispatch('updateCalories', {
            sequence: e.detail.sequence,
            dateStr: e.detail.dateStr,
            value: e.detail.value,
            category: e.detail.category,
            target: e.detail.target,
            callback: e.detail.callback
        })
    }

    const deleteCalories = (e) => {
        dispatch('deleteCalories', {
            sequence: e.detail.sequence,
            dateStr: e.detail.dateStr,
            target: e.detail.target,
            callback: e.detail.callback
        })
    }

    const onAddCalories = (e) => {
        modalStore.trigger({
            type: 'component',
            component: 'trackerModal',
            meta: {
                categories: categories
            },
            response: (e) =>  {
                console.log(e);
                if (e.detail.type === 'add') addCalories(e.detail);
                if (e.detail.close) modalStore.close();
            }
        });
    }

    const onEdit = (e) => {
        modalStore.trigger({
            type: 'component',
            component: 'trackerModal',
            meta: {
                entries: entries,
                categories: categories
            },
            response: (e) => {
                if (e.detail.type === 'update')  updateCalories(e.detail);
                else if (e.detail.type === 'remove') deleteCalories(e.detail);
                if (!e || e.detail.close) modalStore.close();
            }
        });
    }

    /**
     * @param entries {Array<CalorieTrackerEntry>}
     */
    const calculateDeficit = (entries) => {
        const total = entries.reduce((totalCalories, entry) => totalCalories + entry.amount, 0);

        return total - $currentGoal.targetCalories;
    }
</script>

<div class="flex flex-col grow gap-4 justify-evenly text-center">
    <h2 class="h3">
        Calorie Tracker
    </h2>
    <div class="self-center">
        <TrackerRadial entries={entries.map(e => e.amount)} />
    </div>
    {#if $currentGoal}
        {@const deficit = calculateDeficit(entries)}
    <div>
        {#if deficit < 0}
            <p>You still have {Math.abs(deficit)}kcal left for the day. Good job!</p>
        {:else if deficit === 0}
            <p>A spot landing. How did you even do that? There's {deficit} left.</p>
        {:else if deficit > 0 && (deficit + $currentGoal.targetCalories) < $currentGoal.maximumCalories}
            <p>You exceeded your daily target by {deficit}kcal. Days like these happen.</p>
        {:else}
            <p>With a {deficit}kcal surplus, you reached the red zone. Eating over your TDEE causes long term weight gain.</p>
        {/if}
    </div>
    {/if}
    <div>
        <TrackerInput
            bind:value={caloriesQuickAdd}
            on:add={addCaloriesQuickly}
            compact={true}
            unit={'kcal'}
        />
    </div>
    <div class="flex">
            <div class="btn-group variant-filled w-fit grow">
                <button class="w-1/2" aria-label="add calories" on:click={onAddCalories}><Plus/></button>
                <button class="w-1/2" aria-label="edit calories" on:click={onEdit}><Edit/></button>
            </div>
    </div>
</div>
