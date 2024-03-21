import { BmiCategory, Category } from '$lib/api/model.js';

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
export const categoriesAsKeyValue = Object.keys(Category).map((key) => {
	return {
		label: key,
		value: Category[key]
	};
});
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
 * @param {Category} category
 * @returns {String}
 */
export const getCategoryValueAsKey = (category) => {
	return categoriesAsKeyValue.filter((cat) => cat.value === category).map((cat) => cat.label)[0];
};

/**
 * @param {{}} obj
 */
export function enumKeys(obj) {
	return Object.keys(obj).filter((k) => Number.isNaN(+k));
}
