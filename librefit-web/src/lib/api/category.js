/**
 * @param foodCategories {Array<FoodCategory>}
 * @param shortvalue {string}
 */
export const getFoodCategoryLongvalue = (foodCategories, shortvalue) => {
	return foodCategories.filter((fc) => fc.shortvalue === shortvalue)[0].longvalue;
};

/** @param calorieTrackerEntries {Array<CalorieTrackerEntry>} */
export const skimCategories = (calorieTrackerEntries) => {
	return new Set(calorieTrackerEntries.map((entry) => entry.category));
};
