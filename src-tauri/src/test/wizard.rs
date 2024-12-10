use crate::calc::wizard::{
    calculate, BmiCategory, CalculationGoal, CalculationSex, WizardInput, WizardResult,
};
use validator::Validate;

#[test]
pub fn calculate_weight_loss_for_men() {
    let input: WizardInput = WizardInput {
        age: 30,
        weight: 90.0,
        height: 180.0,
        sex: CalculationSex::MALE,
        activity_level: 1.5,
        weekly_difference: 5,
        calculation_goal: CalculationGoal::LOSS,
    };

    let wizard_result = calculate(input);

    assert_eq!(wizard_result.is_ok(), true);

    let result = wizard_result.unwrap();

    assert_eq!(1995.0, result.bmr);
    assert_eq!(500.0, result.deficit);
    assert_eq!(28.0, result.bmi);
    assert_eq!(2993.0, result.tdee);
    assert_eq!(BmiCategory::OVERWEIGHT, result.bmi_category);
    assert_eq!(20, result.target_bmi_lower);
    assert_eq!(25, result.target_bmi_upper);
    assert_eq!(65.0, result.target_weight_lower);
    assert_eq!(81.0, result.target_weight_upper);
    assert_eq!(73.0, result.target_weight);
    assert_eq!(2493.0, result.target);
    assert_eq!(238, result.duration_days);
}

#[test]
pub fn calculate_weight_gain_for_women() {
    let input = WizardInput {
        age: 25,
        weight: 52.0,
        height: 155.0,
        weekly_difference: 1,
        activity_level: 1.25,
        calculation_goal: CalculationGoal::GAIN,
        sex: CalculationSex::FEMALE,
    };

    let wizard_result = calculate(input);

    assert_eq!(wizard_result.is_ok(), true);

    let result = wizard_result.unwrap();

    assert_eq!(1316.0, result.bmr);
    assert_eq!(100.0, result.deficit);
    assert_eq!(22.0, result.bmi);
    assert_eq!(1645.0, result.tdee);
    assert_eq!(BmiCategory::STANDARD_WEIGHT, result.bmi_category);
    assert_eq!(20, result.target_bmi_lower);
    assert_eq!(25, result.target_bmi_upper);
    assert_eq!(22, result.target_bmi);
    assert_eq!(48.0, result.target_weight_lower);
    assert_eq!(60.0, result.target_weight_upper);
    assert_eq!(54.0, result.target_weight);
    assert_eq!(1745.0, result.target);
    assert_eq!(140, result.duration_days);
}

#[test]
fn calculate_underweight_classification_for_men() {
    let input_underweight = WizardInput {
        age: 25,
        weight: 60.0,
        height: 180.0,
        weekly_difference: 0,
        activity_level: 1.0,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::MALE,
    };

    let result_underweight = calculate(input_underweight).unwrap();

    assert_eq!(result_underweight.bmi_category, BmiCategory::UNDERWEIGHT);
}

#[test]
fn calculate_obese_classification_for_men() {
    let input_obese = WizardInput {
        age: 25,
        weight: 130.0,
        height: 180.0,
        weekly_difference: 0,
        activity_level: 1.0,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::MALE,
    };

    let result_obese = calculate(input_obese).unwrap();

    assert_eq!(result_obese.bmi_category, BmiCategory::OBESE);
}

#[test]
fn calulcate_severely_obese_classification_for_men() {
    let input_severely_obese = WizardInput {
        age: 45,
        weight: 150.0,
        height: 180.0,
        weekly_difference: 0,
        activity_level: 1.0,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::MALE,
    };

    let result_severely_obese = calculate(input_severely_obese).unwrap();

    assert_eq!(
        result_severely_obese.bmi_category,
        BmiCategory::SEVERELY_OBESE
    );
}

#[test]
fn calculate_obese_classification_for_women() {
    let input_obese = WizardInput {
        age: 30,
        weight: 80.0,
        height: 160.0,
        weekly_difference: 0,
        activity_level: 1.0,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::FEMALE,
    };

    let result_obese = calculate(input_obese).unwrap();

    assert_eq!(result_obese.bmi_category, BmiCategory::OBESE);
}

#[test]
fn calculate_underweight_classification_for_women() {
    let input_underweight = WizardInput {
        age: 18,
        weight: 40.0,
        height: 150.0,
        weekly_difference: 0,
        activity_level: 1.0,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::FEMALE,
    };

    let result_underweight = calculate(input_underweight).unwrap();

    assert_eq!(result_underweight.bmi_category, BmiCategory::UNDERWEIGHT);
}

#[test]
fn calulcate_severely_obese_classification_for_women() {
    let input_severely_obese = WizardInput {
        age: 45,
        weight: 120.0,
        height: 165.0,
        weekly_difference: 0,
        activity_level: 1.0,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::FEMALE,
    };

    let result_severely_obese = calculate(input_severely_obese).unwrap();

    assert_eq!(
        result_severely_obese.bmi_category,
        BmiCategory::SEVERELY_OBESE
    );
}

#[test]
fn return_error_for_invalid_age() {
    let invalid_age = WizardInput {
        age: 13,
        weight: 40.0,
        height: 140.0,
        weekly_difference: 0,
        activity_level: 1.5,
        calculation_goal: CalculationGoal::GAIN,
        sex: CalculationSex::MALE,
    };

    let validation_errors = invalid_age.validate().unwrap_err();
    let age_error = validation_errors.field_errors().get("age").unwrap()[0]
        .code
        .clone();
}

#[test]
fn return_validation_errors() {
    let invalid_input = WizardInput {
        age: 13,
        weight: 20.0,
        height: 340.0,
        weekly_difference: 8,
        activity_level: 0.5,
        calculation_goal: CalculationGoal::LOSS,
        sex: CalculationSex::FEMALE,
    };

    let validation_errors = invalid_input.validate().unwrap_err();
    let age_error = validation_errors.field_errors().get("age").unwrap()[0]
        .code
        .clone();

    let weight_error = validation_errors.field_errors().get("weight").unwrap()[0]
        .code
        .clone();

    let height_error = validation_errors.field_errors().get("height").unwrap()[0]
        .code
        .clone();

    let weekly_difference_error = validation_errors
        .field_errors()
        .get("weekly_difference")
        .unwrap()[0]
        .code
        .clone();

    let activity_level_error = validation_errors
        .field_errors()
        .get("activity_level")
        .unwrap()[0]
        .code
        .clone();

    assert_eq!(age_error, "validation.wizard.age");
    assert_eq!(weight_error, "validation.wizard.weight");
    assert_eq!(height_error, "validation.wizard.height");
    assert_eq!(
        weekly_difference_error,
        "validation.wizard.weekly_difference"
    );
    assert_eq!(activity_level_error, "validation.wizard.activity_level");
}
