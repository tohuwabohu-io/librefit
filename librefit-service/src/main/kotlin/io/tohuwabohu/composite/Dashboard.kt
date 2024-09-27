package io.tohuwabohu.composite

import io.tohuwabohu.crud.*

data class Dashboard(
    val userData: LibreUser,
    val foodCategories: List<FoodCategory>,
    val calorieTarget: CalorieTarget?,
    val caloriesTodayList: List<CalorieTracker>?,
    val caloriesWeekList: List<CalorieTracker>?,
    val weightTarget: WeightTarget?,
    val weightTodayList: List<WeightTracker>?,
    val weightMonthList: List<WeightTracker>?
)
