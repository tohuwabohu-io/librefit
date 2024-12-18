use diesel::{QueryResult, RunQueryDsl, SelectableHelper, SqliteConnection};

use crate::crud::db::model::BodyData;
use crate::crud::db::schema::body_data::dsl::body_data;

/// Return the initial values entered through the initial setup
pub fn get_body_data(conn: &mut SqliteConnection) -> QueryResult<BodyData> {
    body_data.first(conn)
}

pub fn update_body_data(
    conn: &mut SqliteConnection,
    age: &i32,
    height: &f32,
    weight: &f32,
    sex: &String,
) -> QueryResult<BodyData> {
    body_data.first(conn).map(|mut body: BodyData| {
        body.age = *age;
        body.height = *height;
        body.weight = *weight;
        body.sex = sex.clone();

        diesel::update(body_data)
            .set(&body)
            .returning(BodyData::as_returning())
            .get_result(conn)
    })?
}
