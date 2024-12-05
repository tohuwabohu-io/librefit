use crate::crud::db::model::{NewWeightTarget, NewWeightTracker, WeightTarget, WeightTracker};
use diesel::prelude::*;
use diesel::sqlite::SqliteConnection;

/// Create a new weight target
pub fn create_weight_target(
    conn: &mut SqliteConnection,
    new_target: NewWeightTarget,
) -> QueryResult<WeightTarget> {
    use crate::crud::db::schema::weight_target::dsl::*;

    diesel::insert_into(weight_target)
        .values(&new_target)
        .returning(WeightTarget::as_returning())
        .get_result(conn)
}

/// Retrieve all weight targets
pub fn get_weight_targets(conn: &mut SqliteConnection) -> QueryResult<Vec<WeightTarget>> {
    use crate::crud::db::schema::weight_target::dsl::*;

    weight_target.load::<WeightTarget>(conn)
}

/// Update a weight target by ID
pub fn update_weight_target(
    conn: &mut SqliteConnection,
    target_id: i32,
    updated_target: NewWeightTarget,
) -> QueryResult<WeightTarget> {
    use crate::crud::db::schema::weight_target::dsl::*;

    diesel::update(weight_target.filter(id.eq(target_id)))
        .set(&updated_target)
        .returning(WeightTarget::as_returning())
        .get_result(conn)
}

/// Delete a weight target by ID
pub fn delete_weight_target(conn: &mut SqliteConnection, target_id: i32) -> QueryResult<usize> {
    use crate::crud::db::schema::weight_target::dsl::*;

    diesel::delete(weight_target.filter(id.eq(target_id))).execute(conn)
}

/// Insert a new weight entry to the tracker
pub fn create_weight_tracker_entry(
    conn: &mut SqliteConnection,
    new_entry: NewWeightTracker,
) -> QueryResult<WeightTracker> {
    use crate::crud::db::schema::weight_tracker::dsl::*;

    diesel::insert_into(weight_tracker)
        .values(&new_entry)
        .returning(WeightTracker::as_returning())
        .get_result(conn)
}

/// Retrieve all weight tracker entries
pub fn get_weight_tracker_entries(conn: &mut SqliteConnection) -> QueryResult<Vec<WeightTracker>> {
    use crate::crud::db::schema::weight_tracker::dsl::*;

    weight_tracker.load::<WeightTracker>(conn)
}

/// Update a weight tracker entry by ID
pub fn update_weight_tracker_entry(
    conn: &mut SqliteConnection,
    tracker_id: i32,
    updated_entry: NewWeightTracker,
) -> QueryResult<WeightTracker> {
    use crate::crud::db::schema::weight_tracker::dsl::*;

    diesel::update(weight_tracker.filter(id.eq(tracker_id)))
        .set(&updated_entry)
        .returning(WeightTracker::as_returning())
        .get_result(conn)
}

/// Delete a weight tracker entry by ID
pub fn delete_weight_tracker_entry(
    conn: &mut SqliteConnection,
    tracker_id: i32,
) -> QueryResult<usize> {
    use crate::crud::db::schema::weight_tracker::dsl::*;

    diesel::delete(weight_tracker.filter(id.eq(tracker_id))).execute(conn)
}
