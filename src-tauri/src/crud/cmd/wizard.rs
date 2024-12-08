use crate::calc;
use crate::calc::wizard::{Wizard, WizardInput, WizardResult, 
    WizardTargetDateInput, WizardTargetDateResult,
    WizardTargetWeightInput, WizardTargetWeightResult};
use crate::crud::db;
use crate::crud::db::connection::create_db_connection;
use diesel::Connection;
use tauri::command;
use crate::crud::cmd::error_handler::handle_error;

#[command]
pub fn wizard_calculate_tdee(input: WizardInput) -> Result<WizardResult, String> {
    log::info!(">>> wizard_calculate_tdee: {:?}", input);

    let wizard_result = calc::wizard::calculate(input);

    log::info!(">>> result={:?}", wizard_result);

    Ok(wizard_result)
}

#[command]
pub fn wizard_create_targets(input: Wizard) -> Result<(), String> {
    log::info!(">>> wizard_create_targets: {:#?}", input);

    let manager = &mut create_db_connection();

    manager.transaction(|conn| {
        db::repo::weight::create_weight_target(conn, &input.weight_target)?;
        db::repo::weight::create_weight_tracker_entry(conn, &input.weight_tracker)?;
        db::repo::calories::create_calorie_target(conn, &input.calorie_target)?;

        Ok(())
    }).map_err(handle_error)
}

#[command]
pub fn wizard_calculate_for_target_date(input: WizardTargetDateInput) -> Result<WizardTargetDateResult, String> {
    log::info!(">>> wizard_calculate_for_target_date: {:?}", input);
    
    let wizard_result = calc::wizard::calculate_for_target_date(&input);
    
    log::info!(">>> result={:?}", wizard_result);
    
    Ok(wizard_result)
}

#[command]
pub fn wizard_calculate_for_target_weight(input: WizardTargetWeightInput) -> Result<WizardTargetWeightResult, String> {
    log::info!(">>> wizard_calculate_for_target_weight {:?}", input);

    let wizard_result = calc::wizard::calculate_for_target_weight(&input);

    log::info!(">>> result={:?}", wizard_result);

    Ok(wizard_result)
}
