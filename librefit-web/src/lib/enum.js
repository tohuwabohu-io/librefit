import { BmiCategory } from '$lib/api/model.js';

/**
 * @readonly
 * @enum {string}
 */
export const DataViews = {
	Week: 'WEEK',
	Month: 'MONTH',
	Year: 'YEAR'
};

/**
 * @readonly
 * @enum {string}
 */
export const WizardOptions = {
	Default: 'DEFAULT',
	Recommended: 'RECOMMENDED',
	Custom_weight: 'CUSTOM_WEIGHT',
	Custom_date: 'CUSTOM_DATE',
	Custom: 'CUSTOM'
};

/**
 * @type {{label: String, value: String}[]}
 */
export const bmiCategoriesAsKeyValue = Object.keys(BmiCategory).map((key) => {
	return {
		label: key.replace('_', ' '),
		value: BmiCategory[key]
	};
});

/**
 * @param {{}} obj
 */
export function enumKeys(obj) {
	return Object.keys(obj).filter((k) => Number.isNaN(+k));
}

export const getBmiCategoryDisplayValue = (bmiCategory) => {
	return bmiCategory.toLowerCase().replace('_', ' ')
}
