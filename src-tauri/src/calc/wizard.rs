use crate::crud::db::model::{CalorieTarget, NewCalorieTarget, NewWeightTarget, NewWeightTracker, WeightTarget, WeightTracker};
use chrono::NaiveDate;
use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use validator::{Validate, ValidationError};

#[derive(Serialize, Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct Wizard {
    pub calorie_target: NewCalorieTarget,
    pub weight_target: NewWeightTarget,
    pub weight_tracker: NewWeightTracker,
}

#[derive(Validate, Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct WizardInput {
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

#[derive(Serialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct WizardResult {
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
#[serde(rename_all = "camelCase")]
pub struct WizardTargetWeightInput {
    age: i32,
    sex: CalculationSex,
    current_weight: f32,
    height: f32,
    target_weight: f32,
    #[serde(deserialize_with = "crate::crud::serde::date::deserialize")]
    start_date: NaiveDate,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct WizardTargetWeightResult {
    #[serde(deserialize_with = "crate::crud::serde::date::deserialize")]
    date_per_rate: HashMap<i32, String>,
    target_classification: BmiCategory,
    warning: bool,
    message: String,
}

#[derive(Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct WizardTargetDateInput {
    age: i32,
    sex: CalculationSex,
    current_weight: f32,
    height: f32,
    calculation_goal: CalculationGoal,
    #[serde(deserialize_with = "crate::crud::serde::date::deserialize")]
    target_date: NaiveDate,
}
/*
#[derive(Serialize, Deserialize)]
pub struct WizardTargetDateResult {
    result_by_rate: HashMap<i32, WizardResult>,
}
*/
#[derive(Serialize, Deserialize, Debug)]
enum CalculationGoal {
    GAIN,
    LOSS,
}

#[derive(Serialize, Deserialize, Debug)]
enum CalculationSex {
    MALE,
    FEMALE,
}

#[derive(Serialize, Deserialize, Debug)]
enum BmiCategory {
    UNDERWEIGHT,
    STANDARD_WEIGHT,
    OVERWEIGHT,
    OBESE,
    SEVERELY_OBESE,
}

#[derive(Serialize, Deserialize, Debug)]
enum WizardRecommendation {
    HOLD,
    LOSE,
    GAIN,
}

#[derive(Serialize, Deserialize)]
struct ErrorDescription {
    property_name: String,
    message: String,
}

fn validate_activity_level(activity_level: f32) -> Result<(), ValidationError> {
    match activity_level {
        1f32 | 1.25f32 | 1.5f32 | 1.75f32 | 2f32 => Ok(()),
        _ => Err(ValidationError::new("Please enter a valid activity level.")),
    }
}

pub fn calculate(wizard_input: WizardInput) -> WizardResult {
    let target_bmi_range = calculate_target_bmi(&wizard_input.age);

    let bmr = calculate_bmr(
        &wizard_input.sex,
        &wizard_input.weight,
        &wizard_input.height,
        &wizard_input.age,
    );

    let tdee = calculate_tdee(&wizard_input.activity_level, &bmr);
    let deficit = calculate_deficit(&wizard_input.weekly_difference);
    let target = calculate_target(&wizard_input.calculation_goal, &tdee, &deficit);

    let bmi = calculate_bmi(&wizard_input.weight, &wizard_input.height);
    let bmi_category = calculate_bmi_category(&wizard_input.sex, &bmi);

    let target_bmi_lower = target_bmi_range.start().clone();
    let target_bmi_upper = target_bmi_range.end().clone();
    let target_bmi = (target_bmi_upper + target_bmi_lower) / 2;

    let target_weight = calculate_target_weight(&target_bmi_range, &wizard_input.height);
    let target_weight_lower =
        calculate_target_weight_lower(&target_bmi_range, &wizard_input.height);
    let target_weight_upper =
        calculate_target_weight_upper(&target_bmi_range, &wizard_input.height);

    let recommendation = calculate_recommendation(&bmi_category);

    let mut wizard_result = WizardResult {
        bmr,
        tdee,
        deficit,
        target,
        bmi,
        bmi_category,
        recommendation,
        target_bmi,
        target_bmi_upper,
        target_bmi_lower,
        target_weight,
        target_weight_upper,
        target_weight_lower,
        duration_days: 0,
        duration_days_upper: 0,
        duration_days_lower: 0,
    };

    calculate_duration_days_total(&wizard_input, &mut wizard_result);

    wizard_result
}

fn calculate_bmr(sex: &CalculationSex, weight: &f32, height: &f32, age: &i32) -> f32 {
    match sex {
        CalculationSex::MALE => (66.0 + 13.7 * weight + 5.0 * height - 6.8 * (*age as f32)).round(),
        CalculationSex::FEMALE => {
            (655.0 + 9.6 * weight + 1.8 * height - 4.7 * (*age as f32)).round()
        }
    }
}

fn calculate_tdee(activity_level: &f32, bmr: &f32) -> f32 {
    (activity_level * bmr).round()
}

fn calculate_deficit(weekly_difference: &i32) -> f32 {
    (weekly_difference.clone() as f32 / 10.0 * 7000.0 / 7.0).round()
}

fn calculate_target(calculation_goal: &CalculationGoal, tdee: &f32, deficit: &f32) -> f32 {
    match calculation_goal {
        CalculationGoal::GAIN => tdee + deficit,
        CalculationGoal::LOSS => tdee - deficit,
    }
}

fn calculate_bmi(weight: &f32, height: &f32) -> f32 {
    (weight / ((height / 100.0).powi(2))).round()
}

fn calculate_bmi_category(sex: &CalculationSex, bmi: &f32) -> BmiCategory {
    match sex {
        CalculationSex::FEMALE => match bmi {
            0.0..18.0 => BmiCategory::UNDERWEIGHT,
            19.0..24.0 => BmiCategory::STANDARD_WEIGHT,
            25.0..30.0 => BmiCategory::OVERWEIGHT,
            31.0..40.0 => BmiCategory::OBESE,
            _ => BmiCategory::SEVERELY_OBESE,
        },
        CalculationSex::MALE => match bmi {
            0.0..19.0 => BmiCategory::UNDERWEIGHT,
            20.0..25.0 => BmiCategory::STANDARD_WEIGHT,
            26.0..30.0 => BmiCategory::OVERWEIGHT,
            31.0..40.0 => BmiCategory::OBESE,
            _ => BmiCategory::SEVERELY_OBESE,
        },
    }
}

fn calculate_target_bmi(age: &i32) -> std::ops::RangeInclusive<i32> {
    match age {
        19..=24 => 19..=24,
        25..=34 => 20..=25,
        35..=44 => 21..=26,
        45..=54 => 22..=27,
        55..=64 => 23..=28,
        _ => 24..=29,
    }
}

fn calculate_target_weight(target_bmi: &std::ops::RangeInclusive<i32>, height: &f32) -> f32 {
    (((target_bmi.start() + target_bmi.end()) as f32 / 2.0) * (height / 100.0).powi(2)).round()
}

fn calculate_target_weight_lower(target_bmi: &std::ops::RangeInclusive<i32>, height: &f32) -> f32 {
    (*(target_bmi.start()) as f32 * (height / 100.0).powi(2)).round()
}

fn calculate_target_weight_upper(target_bmi: &std::ops::RangeInclusive<i32>, height: &f32) -> f32 {
    (*(target_bmi.end()) as f32 * (height / 100.0).powi(2)).round()
}

fn calculate_duration_days_total(wizard_input: &WizardInput, wizard_result: &mut WizardResult) {
    match wizard_input.calculation_goal {
        CalculationGoal::GAIN => {
            wizard_result.duration_days = calculate_duration_days(
                wizard_result.target_weight,
                wizard_input.weight,
                wizard_result.deficit,
            );
            wizard_result.duration_days_upper = calculate_duration_days(
                wizard_result.target_weight_upper,
                wizard_input.weight,
                wizard_result.deficit,
            );
            wizard_result.duration_days_lower = calculate_duration_days(
                wizard_result.target_weight_lower,
                wizard_input.weight,
                wizard_result.deficit,
            );
        }
        CalculationGoal::LOSS => {
            wizard_result.duration_days = calculate_duration_days(
                wizard_input.weight,
                wizard_result.target_weight,
                wizard_result.deficit,
            );
            wizard_result.duration_days_upper = calculate_duration_days(
                wizard_input.weight,
                wizard_result.target_weight_upper,
                wizard_result.deficit,
            );
            wizard_result.duration_days_lower = calculate_duration_days(
                wizard_input.weight,
                wizard_result.target_weight_lower,
                wizard_result.deficit,
            );
        }
    }
}

fn calculate_duration_days(weight: f32, target_weight: f32, deficit: f32) -> i32 {
    ((weight - target_weight) * 7000.0 / deficit).floor() as i32
}

fn calculate_recommendation(bmi_category: &BmiCategory) -> WizardRecommendation {
    match bmi_category {
        BmiCategory::STANDARD_WEIGHT => WizardRecommendation::HOLD,
        BmiCategory::UNDERWEIGHT => WizardRecommendation::GAIN,
        BmiCategory::OVERWEIGHT => WizardRecommendation::LOSE,
        BmiCategory::OBESE => WizardRecommendation::LOSE,
        BmiCategory::SEVERELY_OBESE => WizardRecommendation::LOSE,
    }
}