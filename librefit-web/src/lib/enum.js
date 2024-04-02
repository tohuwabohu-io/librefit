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
