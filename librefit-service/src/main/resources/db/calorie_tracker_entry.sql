create table calorie_tracker_entry
(
    id          bigint not null
        primary key,
    added       timestamp,
    amount      real,
    description varchar(255),
    updated     timestamp,
    user_id     bigint
);

alter table calorie_tracker_entry
    owner to librefit;

