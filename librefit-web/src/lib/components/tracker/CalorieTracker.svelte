<script>
    import TrackerRadial from '$lib/components/TrackerRadial.svelte';
    import {createEventDispatcher} from 'svelte';
    import {getDateAsStr, getDaytimeFoodCategory} from '$lib/date.js';
    import Plus from '$lib/assets/icons/plus.svg';
    import Edit from '$lib/assets/icons/pencil.svg?component';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import TrackerInput from '$lib/components/TrackerInput.svelte';

    const modalStore = getModalStore();

    /** @type Array<CalorieTracker> */
    export let calorieTracker = [];

    /** @type Array<FoodCategory> */
    export let categories;

    /** @type CalorieTarget */
    export let calorieTarget;

    const dispatch = createEventDispatcher();

    let caloriesQuickAdd;
    let deficit;

    $: if (calorieTarget) {
        deficit = calculateDeficit(calorieTracker)
    }

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
                if (e) {
                    if (e.detail.type === 'add') addCalories(e.detail);
                    if (e.detail.close) modalStore.close();
                } else modalStore.close();
            }
        });
    }

    const onEdit = (e) => {
        modalStore.trigger({
            type: 'component',
            component: 'trackerModal',
            meta: {
                entries: calorieTracker,
                categories: categories
            },
            response: (e) => {
                if (e) {
                    if (e.detail.type === 'update')  updateCalories(e.detail);
                    else if (e.detail.type === 'remove') deleteCalories(e.detail);

                    if (e.detail.close) modalStore.close();
                } else modalStore.close();
            }
        });
    }

    /**
     * @param entries {Array<CalorieTracker>}
     */
    const calculateDeficit = (entries) => {
        const total = entries.reduce((totalCalories, entry) => totalCalories + entry.amount, 0);

        return total - calorieTarget.targetCalories;
    }
</script>

<div class="flex flex-col gap-4 justify-between text-center h-full xl:w-80">
    <h2 class="h3">
        Calorie Tracker
    </h2>
    <div class="flex flex-col w-fit h-full justify-between gap-4 pt-4">
        <div class="self-center">
            <TrackerRadial entries={calorieTracker.map(e => e.amount)} {calorieTarget} />
        </div>
        {#if calorieTarget}
            <div>
                {#if deficit < 0}
                    <p>You still have {Math.abs(deficit)}kcal left for the day. Good job!</p>
                {:else if deficit === 0}
                    <p>A spot landing. How did you even do that? There's {deficit}kcal left.</p>
                {:else if deficit > 0 && (deficit + calorieTarget.targetCalories) <= calorieTarget.maximumCalories}
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
    </div>

    <div class="flex">
        <div class="btn-group variant-filled w-fit grow">
            <button class="w-1/2" aria-label="add calories" on:click={onAddCalories}>
                <span>
                    <Plus/>
                </span>
                <span>
                    Add
                </span>
            </button>
            <button class="w-1/2" aria-label="edit calories" on:click={onEdit}>
                <span>
                    <Edit/>
                </span>
                <span>
                    Edit
                </span>
            </button>
        </div>
    </div>
</div>
