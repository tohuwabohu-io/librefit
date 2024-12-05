use diesel::sqlite::SqliteConnection;
use diesel::Connection;
use std::env;

pub struct DbConnection {
    pub conn: SqliteConnection,
}

impl DbConnection {
    pub fn new() -> Self {
        let db_url= env::var("DATABASE_URL").expect("DATABASE_URL must be set");
        let conn = SqliteConnection::establish(&db_url)
            .expect(&format!("Error connecting to database {}", db_url));
        
        Self { conn }
    }
}

pub fn create_db_connection() -> SqliteConnection {
    let db_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");

    SqliteConnection::establish(&db_url)
        .expect(&format!("Error connecting to database {}", db_url))
}
