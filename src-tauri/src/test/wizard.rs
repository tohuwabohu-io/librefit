use crate::calc::wizard::{
    calculate, BmiCategory, CalculationGoal, CalculationSex, WizardInput, WizardResult,
};

#[test]
pub fn calculate_weight_loss_for_men() {
    let input: WizardInput = WizardInput {
        age: 30,
        weight: 90f32,
        height: 180f32,
        sex: CalculationSex::MALE,
        activity_level: 1.5,
        weekly_difference: 5,
        calculation_goal: CalculationGoal::LOSS,
    };

    let result: WizardResult = calculate(input);

    assert_eq!(1995f32, result.bmr);
    assert_eq!(500f32, result.deficit);
    assert_eq!(28f32, result.bmi);
    assert_eq!(2992f32, result.tdee);
    assert_eq!(BmiCategory::OVERWEIGHT, result.bmi_category);
    assert_eq!(20, result.target_bmi_lower);
    assert_eq!(25, result.target_bmi_upper);
    assert_eq!(65f32, result.target_weight_lower);
    assert_eq!(81f32, result.target_weight_upper);
    assert_eq!(73f32, result.target_weight);
    assert_eq!(2492f32, result.target);
    assert_eq!(238, result.duration_days);
}

#[test]
pub fn calculate_weight_gain_for_women() {
    let input = WizardInput {
        age: 25,
        weight: 52f32,
        height: 155f32,
        weekly_difference: 1,
        activity_level: 1.25f32,
        calculation_goal: CalculationGoal::GAIN,
        sex: CalculationSex::FEMALE,
    };

    let result: WizardResult = calculate(input);

    assert_eq!(1316f32, result.bmr);
    assert_eq!(100f32, result.deficit);
    assert_eq!(22f32, result.bmi);
    assert_eq!(1645f32, result.tdee);
    assert_eq!(BmiCategory::STANDARD_WEIGHT, result.bmi_category);
    assert_eq!(20, result.target_bmi_lower);
    assert_eq!(25, result.target_bmi_upper);
    assert_eq!(22, result.target_bmi);
    assert_eq!(48f32, result.target_weight_lower);
    assert_eq!(60f32, result.target_weight_upper);
    assert_eq!(54f32, result.target_weight);
    assert_eq!(1745f32, result.target);
    assert_eq!(140, result.duration_days);
}
