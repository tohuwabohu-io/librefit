pub mod crud;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .setup(|app| {
            if cfg!(debug_assertions) {
                app.handle().plugin(
                    tauri_plugin_log::Builder::default()
                        .level(log::LevelFilter::Info)
                        .build(),
                )?;
            }
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            crud::cmd::dashboard::daily_dashboard,
            crud::cmd::calorie::create_calorie_tracker_entry,
            crud::cmd::calorie::update_calorie_tracker_entry,
            crud::cmd::calorie::delete_calorie_tracker_entry
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
