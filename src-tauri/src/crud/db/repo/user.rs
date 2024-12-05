use crate::crud::db::model::LibreUser;
use crate::crud::db::schema::libre_user::dsl::libre_user;
use diesel::{QueryResult, RunQueryDsl, SqliteConnection};

pub fn get_user(conn: &mut SqliteConnection) -> QueryResult<LibreUser> {
    libre_user.first(conn)
}
