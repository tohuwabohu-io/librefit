use diesel::{QueryResult, RunQueryDsl, SqliteConnection};
use crate::crud::db::model::FoodCategory;
use crate::crud::db::schema::food_category::dsl::food_category;

pub fn get_food_categories(
    conn: &mut SqliteConnection
) -> QueryResult<Vec<FoodCategory>> {
    food_category.load::<FoodCategory>(conn)
}