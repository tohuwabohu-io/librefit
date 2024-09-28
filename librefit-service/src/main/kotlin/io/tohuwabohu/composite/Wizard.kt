package io.tohuwabohu.composite

import io.tohuwabohu.crud.CalorieTarget
import io.tohuwabohu.crud.WeightTarget
import io.tohuwabohu.crud.WeightTracker

data class Wizard(
    val calorieTarget: CalorieTarget,
    val weightTarget: WeightTarget,
    val weightTracker: WeightTracker
)
