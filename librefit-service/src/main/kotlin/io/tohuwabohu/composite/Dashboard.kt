package io.tohuwabohu.composite

import io.tohuwabohu.crud.*

data class Dashboard(
    val userData: LibreUser,
    val foodCategories: List<FoodCategory>,
    val calorieTarget: CalorieTarget?,
    val caloriesTodayList: List<CalorieTrackerEntry>?,
    val caloriesWeekList: List<CalorieTrackerEntry>?,
    val weightTarget: WeightTarget?,
    val weightMonthList: List<WeightTrackerEntry>?
)
