/**
 * @param foodCategories {Array<FoodCategory>}
 * @param shortvalue {string}
 */
export const getFoodCategoryLongvalue = (foodCategories, shortvalue) => {
	return foodCategories.filter((fc) => fc.shortvalue === shortvalue)[0].longvalue;
};
