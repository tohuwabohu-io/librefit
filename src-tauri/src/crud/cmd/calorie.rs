use crate::crud::db::connection::create_db_connection;
use crate::crud::db::model::{CalorieTarget, CalorieTracker, NewCalorieTarget, NewCalorieTracker};
use crate::crud::db::repo::calories;
use tauri::command;

use crate::crud::cmd::error_handler::handle_error;

/// Create a new calorie target
#[command]
fn create_calorie_target_command(new_target: NewCalorieTarget) -> Result<CalorieTarget, String> {
    let conn = &mut create_db_connection(); // Your function to create database connections
    calories::create_calorie_target(conn, new_target).map_err(handle_error)
}

/// Retrieve all calorie targets
#[command]
fn get_calorie_targets_command() -> Result<Vec<CalorieTarget>, String> {
    let conn = &mut create_db_connection();
    calories::get_calorie_targets(conn).map_err(handle_error)
}

/// Update a calorie target by ID
#[command]
fn update_calorie_target_command(
    target_id: i32,
    updated_target: NewCalorieTarget,
) -> Result<CalorieTarget, String> {
    let conn = &mut create_db_connection();
    calories::update_calorie_target(conn, target_id, updated_target).map_err(handle_error)
}

/// Delete a calorie target by ID
#[command]
fn delete_calorie_target_command(target_id: i32) -> Result<usize, String> {
    let conn = &mut create_db_connection();
    calories::delete_calorie_target(conn, target_id).map_err(handle_error)
}

/// Create a new calorie tracker entry
#[command]
fn create_calorie_tracker_entry_command(
    new_entry: NewCalorieTracker,
) -> Result<CalorieTracker, String> {
    let conn = &mut create_db_connection();
    calories::create_calorie_tracker_entry(conn, new_entry).map_err(handle_error)
}

/// Retrieve all calorie tracker entries
#[command]
fn get_calorie_tracker_entries_command() -> Result<Vec<CalorieTracker>, String> {
    let conn = &mut create_db_connection();

    calories::get_calorie_tracker_entries(conn).map_err(handle_error)
}

/// Update a calorie tracker entry by ID
#[command]
fn update_calorie_tracker_entry_command(
    tracker_id: i32,
    updated_entry: NewCalorieTracker,
) -> Result<CalorieTracker, String> {
    let conn = &mut create_db_connection();
    calories::update_calorie_tracker_entry(conn, tracker_id, updated_entry).map_err(handle_error)
}

/// Delete a calorie tracker entry by ID
#[command]
fn delete_calorie_tracker_entry_command(tracker_id: i32) -> Result<usize, String> {
    let conn = &mut create_db_connection();
    calories::delete_calorie_tracker_entry(conn, tracker_id).map_err(handle_error)
}
