/**
 * @param {Array<CalorieTracker>} entries
 * @param {Array<FoodCategory>} foodCategories
 */
export const getAverageCategoryIntake = (entries, foodCategories) => {
	const nonEmpty = entries.filter((e) => e.amount > 0);

	if (nonEmpty.length > 0) {
		const catMap = new Map();

		const sum = nonEmpty.map((e) => e.amount).reduce((a, b) => a + b);
		const dailyAverage = getAverageDailyIntake(entries);

		foodCategories.forEach((cat) => {
			const catEntries = nonEmpty.filter((e) => e.category === cat.shortvalue);

			if (catEntries.length > 0) {
				const catSum = catEntries.map((e) => e.amount).reduce((a, b) => a + b);

				catMap.set(cat.shortvalue, Math.round(dailyAverage * (catSum / sum)));
			}
		});

		return catMap;
	}

	return null;
};

/** @param {Array<CalorieTracker>} entries */
export const getAverageDailyIntake = (entries) => {
	const nonEmpty = entries.filter((e) => e.amount > 0);

	if (nonEmpty.length > 0) {
		const days = new Set(nonEmpty.map((e) => e.added));

		let sum = 0;

		for (let day of days) {
			sum += entries
				.filter((e) => e.added === day)
				.map((e) => e.amount)
				.reduce((a, b) => a + b);
		}

		return Math.round(sum / days.size);
	}

	return 0;
};
