<script>
    import NoScale from '$lib/assets/icons/scale-outline-off.svg?component';
    import Scale from '$lib/assets/icons/scale-outline.svg?component';
    import {convertDateStrToDisplayDateStr, getDateAsStr} from '$lib/util.js';
    import {createEventDispatcher, getContext} from 'svelte';
    import {getModalStore} from '@skeletonlabs/skeleton';

    const currentGoal = getContext('currentGoal');
    const lastEntry = getContext('lastWeight')

    const modalStore = getModalStore();

    const todayDateStr = getDateAsStr(new Date());

    const dispatch = createEventDispatcher();

    const addWeight = (e) => {
        dispatch('addWeight', {
            date: todayDateStr,
            value: e.detail.value
        });
    }

    const updateWeight = (e) => {
        dispatch('updateWeight', {
            sequence: lastEntry.sequence,
            date: lastEntry.added,
            value: e.detail.value
        });
    }

    const deleteWeight = (e) => {
        dispatch('deleteWeight', {
            sequence: e.detail.sequence,
            date: e.detail.date
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
                    if (lastEntry && lastEntry.added === todayDateStr) {
                        updateWeight(e);
                    } else {
                        addWeight(e);
                    }
                }

                modalStore.close();
            }
        });
    }

    const showGoalModal = () => {
        modalStore.trigger({
            type: 'component',
            component: 'goalModal',
            response: (e) => {
                if (!e.cancelled) {
                    updateGoal(e)
                }

                modalStore.close();
            }
        });
    }
</script>

<div class="flex flex-col grow gap-4">
    <div class="flex flex-col gap-4 justify-between">
        <div class="flex flex-col gap-4 text-center items-center">
            {#if $lastEntry}
                <Scale width={100} height={100} />

                <p>
                    Current weight: {$lastEntry.amount}kg ({convertDateStrToDisplayDateStr($lastEntry.added)})
                </p>
            {:else}
                <NoScale width={100} height={100} />

                <p>
                    Nothing tracked yet. Today is a good day to start!
                </p>
            {/if}

            {#if $currentGoal}
                <p>
                    Goal: {$currentGoal.targetWeight}kg @ ({convertDateStrToDisplayDateStr($currentGoal.endDate)})
                </p>
            {:else}
                <p>
                    No goal set up.
                </p>
            {/if}

            <div class="btn-group variant-filled-primary">
                <button on:click|preventDefault={showWeightModal}>
                    Update weight
                </button>
                <button on:click|preventDefault={showGoalModal}>
                    Update goal
                </button>
            </div>
        </div>
    </div>
</div>