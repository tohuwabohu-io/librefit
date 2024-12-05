use serde::{Deserialize, Serialize};
use crate::crud::db::model::{CalorieTarget, CalorieTracker, WeightTarget, WeightTracker};

#[derive(Serialize, Deserialize)]
pub struct Dashboard {
    pub user_data: String,
    pub calorie_target: Vec<CalorieTarget>,
    pub calories_today_list: Vec<CalorieTracker>,
    pub calories_week_list: Vec<CalorieTracker>,
    pub weight_target: WeightTarget,
    pub weight_today_list: Vec<WeightTracker>,
    pub weight_month_list: Vec<WeightTracker>
}
