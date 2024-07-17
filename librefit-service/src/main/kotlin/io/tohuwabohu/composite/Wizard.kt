package io.tohuwabohu.composite

import io.tohuwabohu.crud.CalorieTarget
import io.tohuwabohu.crud.WeightTarget
import io.tohuwabohu.crud.WeightTrackerEntry

data class Wizard(
    val calorieTarget: CalorieTarget,
    val weightTarget: WeightTarget,
    val weightTrackerEntry: WeightTrackerEntry
)
