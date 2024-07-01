<script>
    import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import Target from '$lib/assets/icons/target-arrow.svg?component';
    import {createEventDispatcher} from 'svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';
    import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/date.js';

    /**
     * @type Goal
     */
    export let currentGoal;

    /**
     * @type WeightTrackerEntry
     */
    export let lastEntry;

    const modalStore = getModalStore();
    const todayDateStr = getDateAsStr(new Date());
    const dispatch = createEventDispatcher();

    const addWeight = (e) => {
        dispatch('addWeight', {
            dateStr: todayDateStr,
            value: e.detail.value
        });
    }

    const updateGoal = (e) => {
        dispatch('updateGoal', {
            goal: e
        })
    }
    
    const showWeightModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'weightModal',
            response: (e) => {
                if (e) {
                    addWeight(e);
                }

                modalStore.close();
            }
        });
    }

    const showGoalModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'goalModal',
            meta: { goal: currentGoal },
            response: (e) => {
                if (!e.cancelled) {
                    updateGoal(e)
                }

                modalStore.close();
            }
        });
    }
</script>

<div class="flex flex-col grow gap-4 text-center items-center self-center">
    <h2 class="h3">Your weight</h2>
    {#if lastEntry}
        <Scale width={100} height={100} />

        <p>
            Current weight: {lastEntry.amount}kg ({convertDateStrToDisplayDateStr(lastEntry.added)})
        </p>
    {:else}
        <NoScale width={100} height={100} />

        <p>
            Nothing tracked yet. Today is a good day to start!
        </p>
    {/if}

    {#if currentGoal}
        <p>
            Goal: {currentGoal.targetWeight}kg @ ({convertDateStrToDisplayDateStr(currentGoal.endDate)})
        </p>
    {:else}
        <p>
            No goal set up.
        </p>
    {/if}

    <div class="flex">
        <div class="btn-group variant-filled w-fit grow">
            <button class="w-1/2" on:click={showWeightModal}>
                <span>
                    <Scale/>
                </span>
                <span>
                    Set weight
                </span>
            </button>
            <button class="w-1/2" on:click={showGoalModal}>
                <span>
                    <Target/>
                </span>
                <span>
                    Set target
                </span>
            </button>
        </div>
    </div>
</div>