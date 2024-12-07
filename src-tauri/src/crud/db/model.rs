use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, AsChangeset, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::libre_user)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct LibreUser {
    pub id: i32,
    pub avatar: Option<String>,
    pub name: Option<String>,
}

#[derive(Queryable, Selectable, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::calorie_tracker)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct CalorieTracker {
    pub id: i32,
    pub added: String,
    pub amount: i32,
    pub category: String,
    pub description: Option<String>,
}

#[derive(Queryable, Selectable, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::weight_tracker)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct WeightTracker {
    pub id: i32,
    pub added: String,
    pub amount: f32,
}

#[derive(Queryable, Selectable, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::food_category)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
pub struct FoodCategory {
    pub longvalue: String,
    pub shortvalue: String,
}

#[derive(Queryable, Selectable, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::calorie_target)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct CalorieTarget {
    pub id: i32,
    pub added: String,
    pub end_date: String,
    pub maximum_calories: i32,
    pub start_date: String,
    pub target_calories: i32,
}

#[derive(Queryable, Selectable, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::weight_target)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct WeightTarget {
    pub id: i32,
    pub added: String,
    pub end_date: String,
    pub initial_weight: f32,
    pub start_date: String,
    pub target_weight: f32,
}

// For CalorieTracker
#[derive(Insertable, AsChangeset, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::calorie_tracker)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct NewCalorieTracker {
    pub added: String,
    pub amount: i32,
    pub category: String,
    pub description: String,
}

// For WeightTracker
#[derive(Insertable, AsChangeset, Serialize, Deserialize, Debug)]
#[diesel(table_name = crate::crud::db::schema::weight_tracker)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct NewWeightTracker {
    pub added: String,
    pub amount: f32,
}

// For FoodCategory
#[derive(Insertable, AsChangeset, Serialize, Deserialize)]
#[diesel(table_name = crate::crud::db::schema::food_category)]
pub struct NewFoodCategory {
    pub longvalue: String,
    pub shortvalue: String,
}

// For CalorieTarget
#[derive(Insertable, AsChangeset, Serialize, Deserialize, Debug)]
#[diesel(table_name = crate::crud::db::schema::calorie_target)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct NewCalorieTarget {
    pub added: String,
    pub end_date: String,
    pub maximum_calories: i32,
    pub start_date: String,
    pub target_calories: i32,
}

// For WeightTarget
#[derive(Insertable, AsChangeset, Serialize, Deserialize, Debug)]
#[diesel(table_name = crate::crud::db::schema::weight_target)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
#[serde(rename_all = "camelCase")]
pub struct NewWeightTarget {
    pub added: String,
    pub end_date: String,
    pub initial_weight: f32,
    pub start_date: String,
    pub target_weight: f32,
}
