use diesel::sqlite::SqliteConnection;
use diesel::Connection;
use dotenv::dotenv;
use std::env;

pub fn create_db_connection() -> SqliteConnection {
    dotenv().ok();
    let db_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");

    log::info!("db_url set to {:?}", db_url);

    SqliteConnection::establish(&db_url).expect(&format!("Error connecting to database {}", db_url))
}
