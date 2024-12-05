use crate::crud::cmd::error_handler::handle_error;
use crate::crud::db::connection::create_db_connection;
use crate::crud::db::model::{NewWeightTarget, NewWeightTracker, WeightTarget, WeightTracker};
use crate::crud::db::repo::weight;
use tauri::command;

/// Create a new weight target
#[command]
fn create_weight_target_command(new_target: NewWeightTarget) -> Result<WeightTarget, String> {
    let conn = &mut create_db_connection();
    weight::create_weight_target(conn, new_target).map_err(handle_error)
}

/// Retrieve all weight targets
#[command]
fn get_weight_targets_command() -> Result<Vec<WeightTarget>, String> {
    let conn = &mut create_db_connection();
    weight::get_weight_targets(conn).map_err(handle_error)
}

/// Update a weight target by ID
#[command]
fn update_weight_target_command(
    target_id: i32,
    updated_target: NewWeightTarget,
) -> Result<WeightTarget, String> {
    let conn = &mut create_db_connection();
    weight::update_weight_target(conn, target_id, updated_target).map_err(handle_error)
}

/// Delete a weight target by ID
#[command]
fn delete_weight_target_command(target_id: i32) -> Result<usize, String> {
    let conn = &mut create_db_connection();
    weight::delete_weight_target(conn, target_id).map_err(handle_error)
}

/// Create a new weight tracker entry
#[command]
fn create_weight_tracker_entry_command(
    new_entry: NewWeightTracker,
) -> Result<WeightTracker, String> {
    let conn = &mut create_db_connection();
    weight::create_weight_tracker_entry(conn, new_entry).map_err(handle_error)
}

/// Retrieve all weight tracker entries
#[command]
fn get_weight_tracker_entries_command() -> Result<Vec<WeightTracker>, String> {
    let conn = &mut create_db_connection();
    weight::get_weight_tracker_entries(conn).map_err(handle_error)
}

/// Update a weight tracker entry by ID
#[command]
fn update_weight_tracker_entry_command(
    tracker_id: i32,
    updated_entry: NewWeightTracker,
) -> Result<WeightTracker, String> {
    let conn = &mut create_db_connection();
    weight::update_weight_tracker_entry(conn, tracker_id, updated_entry).map_err(handle_error)
}

/// Delete a weight tracker entry by ID
#[command]
fn delete_weight_tracker_entry_command(tracker_id: i32) -> Result<usize, String> {
    let conn = &mut create_db_connection();
    weight::delete_weight_tracker_entry(conn, tracker_id).map_err(handle_error)
}
