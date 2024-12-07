use crate::calc;
use crate::calc::wizard::{WizardInput, WizardResult};
use tauri::command;

#[command]
pub fn wizard_calculate_tdee(
    input: WizardInput
) -> Result<WizardResult, String> {
    log::info!(">>> wizard_calculate_tdee: {:?}", input);

    let wizard_result = calc::wizard::calculate(input);

    log::info!(">>> result={:?}", wizard_result);

    Ok(wizard_result)
}