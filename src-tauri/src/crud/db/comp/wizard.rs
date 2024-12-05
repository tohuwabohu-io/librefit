use crate::crud::db::model::{CalorieTarget, WeightTarget, WeightTracker};
use chrono::NaiveDate;
use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use validator::{Validate, ValidationError};

#[derive(Serialize, Deserialize)]
pub struct Wizard {
    pub calorie_target: CalorieTarget,
    pub weight_target: WeightTarget,
    pub weight_tracker: WeightTracker
}

#[derive(Validate)]
struct WizardInput {
    #[validate(range(min = 18, max = 99))]
    age: i32,
    sex: CalculationSex,
    #[validate(range(min = 30f32, max = 300f32))]
    weight: f32,
    #[validate(range(min = 100f32, max = 300f32))]
    height: f32,
    #[validate(custom(function = "validate_activity_level"))]
    activity_level: f32,
    #[validate(range(min = 0, max = 7))]
    weekly_difference: i32,
    calculation_goal: CalculationGoal,
}

#[derive(Serialize, Deserialize)]
struct WizardResult {
    bmr: f32,
    tdee: f32,
    deficit: f32,
    target: f32,
    bmi: f32,
    bmi_category: BmiCategory,
    recommendation: WizardRecommendation,
    target_bmi: i32,
    target_bmi_upper: i32,
    target_bmi_lower: i32,
    target_weight: f32,
    target_weight_upper: f32,
    target_weight_lower: f32,
    duration_days: i32,
    duration_days_upper: i32,
    duration_days_lower: i32,
}

#[derive(Deserialize)]
struct WizardTargetWeightInput {
    age: i32,
    sex: CalculationSex,
    current_weight: f32,
    height: f32,
    target_weight: f32,
    #[serde(deserialize_with = "crate::crud::serde::date::deserialize")]
    start_date: NaiveDate,
}

#[derive(Serialize)]
struct WizardTargetWeightResult {
    #[serde(deserialize_with = "crate::crud::serde::date::deserialize")]
    date_per_rate: HashMap<i32, String>,
    target_classification: BmiCategory,
    warning: bool,
    message: String,
}

#[derive(Deserialize)]
struct WizardTargetDateInput {
    age: i32,
    sex: CalculationSex,
    current_weight: f32,
    height: f32,
    calculation_goal: CalculationGoal,
    #[serde(deserialize_with = "crate::crud::serde::date::deserialize")]
    target_date: NaiveDate,
}

#[derive(Serialize, Deserialize)]
struct WizardTargetDateResult {
    result_by_rate: HashMap<i32, WizardResult>,
}

#[derive(Serialize, Deserialize)]
enum CalculationGoal {
    Gain,
    Loss,
}

#[derive(Serialize, Deserialize)]
enum CalculationSex {
    Male,
    Female,
}

#[derive(Serialize, Deserialize)]
enum BmiCategory {
    Underweight,
    StandardWeight,
    Overweight,
    Obese,
    SeverelyObese,
}

#[derive(Serialize, Deserialize)]
enum WizardRecommendation {
    Hold,
    Lose,
    Gain,
}

#[derive(Serialize, Deserialize)]
struct ErrorDescription {
    property_name: String,
    message: String,
}

// Implement `calculate_bmr`, `calculate_tdee`, `calculate_deficit`, `calculate_target`,
// `calculate_bmi`, `calculate_bmi_category`, `calculate_target_bmi`,
// `calculate_target_weight`, `calculate_duration_days`,
// `calculate_duration_days_total`, and others similarly using Rust idioms.
// Replace Java and Kotlin concepts with appropriate Rust alternatives.

fn main() {
    // An example implementation could go here
}

fn validate_activity_level(activity_level: f32) -> Result<(), ValidationError> {
    match activity_level {
        1f32 | 1.25f32 | 1.5f32 | 1.75f32 | 2f32 => Ok(()),
        _ => Err(ValidationError::new("Please enter a valid activity level."))
    }
}
