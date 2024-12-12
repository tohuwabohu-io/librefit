#[macro_use]
extern crate rust_i18n;

i18n!("locales", fallback = "en");

use crate::crud::cmd::calorie::{
    create_calorie_target, create_calorie_tracker_entry, delete_calorie_tracker_entry,
    get_calorie_tracker_dates_in_range, get_calorie_tracker_for_date_range,
    update_calorie_tracker_entry,
};
use crate::crud::cmd::dashboard::daily_dashboard;
use crate::crud::cmd::user::update_user;
use crate::crud::cmd::weight::{
    create_weight_target, create_weight_tracker_entry, delete_weight_tracker_entry,
    get_weight_tracker_for_date_range, update_weight_tracker_entry,
};
use crate::crud::cmd::wizard::{
    wizard_calculate_for_target_date, wizard_calculate_for_target_weight, wizard_calculate_tdee,
    wizard_create_targets,
};

use dotenv::dotenv;
use std::env;
use tauri::path::BaseDirectory;
use tauri::Manager;
use tauri_plugin_fs::FsExt;

pub mod calc;
pub mod crud;
pub mod i18n;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_fs::init())
        .setup(|app| {
            if cfg!(debug_assertions) {
                app.handle().plugin(
                    tauri_plugin_log::Builder::default()
                        .level(log::LevelFilter::Info)
                        .build(),
                )?;
            }

            dotenv().ok();

            let scope = app.fs_scope();
            let _ = scope.allow_directory("res", false);
            _ = scope.allow_directory("migrations", true);

            let resource_path = app
                .path()
                .resolve("res/tracker.db", BaseDirectory::AppData)
                .unwrap();

            env::set_var("DATABASE_URL", resource_path.clone());

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            daily_dashboard,
            create_calorie_tracker_entry,
            update_calorie_tracker_entry,
            delete_calorie_tracker_entry,
            get_calorie_tracker_for_date_range,
            get_calorie_tracker_dates_in_range,
            create_weight_tracker_entry,
            update_weight_tracker_entry,
            delete_weight_tracker_entry,
            get_weight_tracker_for_date_range,
            create_calorie_target,
            create_weight_target,
            update_user,
            wizard_calculate_tdee,
            wizard_create_targets,
            wizard_calculate_for_target_date,
            wizard_calculate_for_target_weight
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
