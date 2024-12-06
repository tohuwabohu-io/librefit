use tauri::command;
use crate::crud::cmd::error_handler::handle_error;
use crate::crud::db::connection::create_db_connection;
use crate::crud::db::model::LibreUser;
use crate::crud::db::repo::user;

#[command]
pub fn update_user(
    user_name: String,
    user_avatar: String
) -> Result<LibreUser, String> {
    log::info!(">>> update_user: user_name={}, user_avatar={}", user_name, user_avatar);
    
    let conn = &mut create_db_connection();
    user::update_user(conn, &user_name, &user_avatar).map_err(handle_error)
}