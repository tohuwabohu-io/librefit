<script>
    import {BmiCategory} from '$lib/api/model.js';
    import {bmiCategoriesAsKeyValue} from '$lib/enum.js';

    const bmiCategories = bmiCategoriesAsKeyValue;

    /** @type {Tdee} */
    export let calculationResult;
</script>
<h2 class="h2">Your result</h2>

<div class="table-container">
    <table class="table table-compact" aria-label="result table">
        <thead>
        <tr>
            <th>Description</th>
            <th colspan="2">Value</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Basal Metabolic Rate</td>
            <td>kcal</td>
            <td>{calculationResult.bmr}</td>
        </tr>

        <tr>
            <td>Total Daily Energy Expediture</td>
            <td>kcal</td>
            <td>{calculationResult.tdee}</td>
        </tr>

        <tr>
            <td>Target deficit</td>
            <td>kcal</td>
            <td>{calculationResult.deficit}</td>
        </tr>

        <tr>
            <td>Target intake</td>
            <td>kcal</td>
            <td>{calculationResult.target}</td>
        </tr>
        </tbody>
    </table>
</div>

<h2 class="h2">Body parameters</h2>

<div class="table-container">
    <table class="table table-compact" aria-label="body parameter table">
        <thead>
        <tr>
            <th>Description</th>
            <th colspan="2">Value</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Age</td>
            <td></td>
            <td>{calculationResult.age}</td>
        </tr>

        <tr>
            <td>Height</td>
            <td>cm</td>
            <td>{calculationResult.height}</td>
        </tr>

        <tr>
            <td>Weight</td>
            <td>kg</td>
            <td>{calculationResult.weight}</td>
        </tr>

        <tr>
            <td>Body Mass Index</td>
            <td></td>
            <td>{calculationResult.bmi}</td>
        </tr>

        <tr>
            <td>Classification</td>
            <td></td>
            <td>{bmiCategories.filter(e => e.value === calculationResult.bmiCategory)[0].label}</td>
        </tr>
        </tbody>
    </table>
</div>


<h2 class="h2">Next steps</h2>
<p>
    Based on your input, your basal metabolic rate is {calculationResult.bmr}kcal. Your daily calorie
    consumption to hold your weight should be around {calculationResult.tdee}kcal.
</p>

<p>
    Having {calculationResult.weight}kg at {calculationResult.height}cm height means you have a BMI of
    {calculationResult.bmi}.

    {#if calculationResult.targetBmi}
        At your age of {calculationResult.age},

        {@const bmiMin = calculationResult.targetBmi[0]}
        {@const bmiMax = calculationResult.targetBmi[1]}

        {#if bmiMin <= calculationResult.bmi && calculationResult.bmi <= bmiMax}
            you are currently in
        {:else}
            you are out of
        {/if}

        the optimal BMI range of {bmiMin} to {bmiMax}, leaving you
        {bmiCategories.filter(e => e.value === calculationResult.bmiCategory)[0].label}.
        Your weight should be around {calculationResult.targetWeight}kg or between
        {calculationResult.targetWeightLower}kg and {calculationResult.targetWeightUpper}kg.
    {/if}
</p>

{#if calculationResult.bmiCategory === BmiCategory.Standard_weight}
<p>
    Being standard weight, you are currently in the optimal weight range.
</p>
{:else if calculationResult.bmiCategory === BmiCategory.Underweight}
<p>
    You are underweight. First of all, it is recommended to consult with a healthcare professional.
</p>
<p>
    To reach the lower bound of the optimal weight within your standard weight range
    ({calculationResult.targetWeightLower}kg - {calculationResult.targetWeightUpper}kg), you will need to
    consume calories at a surplus of {calculationResult.deficit}kcal for {calculationResult.durationDaysLower}
    days. Your caloric intake should be around {calculationResult.target}kcal during that time.
</p>
{:else}
<p>
    You are {bmiCategories.filter(e => e.value === calculationResult.bmiCategory)[0].label.toLowerCase()}.
    {#if BmiCategory.Obese === calculationResult.bmiCategory || BmiCategory.Severely_obese === calculationResult.bmiCategory }
        Please consult with a healthcare professional.
    {/if}
</p>
<p>
    To reach the optimal weight within the standard weight range
    ({calculationResult.targetWeightLower}kg - {calculationResult.targetWeightUpper}kg),
    you will need to consume calories at a difference of {calculationResult.deficit}kcal for
    {calculationResult.durationDaysUpper} days. Your caloric intake should be around {calculationResult.target}kcal during that time.
</p>
{/if}