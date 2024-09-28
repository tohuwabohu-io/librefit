/**
 * @param foodCategories {Array<FoodCategory>}
 * @param shortvalue {string}
 */
export const getFoodCategoryLongvalue = (foodCategories, shortvalue) => {
	return foodCategories.filter((fc) => fc.shortvalue === shortvalue)[0].longvalue;
};

/** @param calorieTracker {Array<CalorieTracker>} */
export const skimCategories = (calorieTracker) => {
	return new Set(calorieTracker.map((entry) => entry.category));
};
