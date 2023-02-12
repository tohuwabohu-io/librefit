create table calorie_tracker
(
    id          serial,
    added       timestamp not null,
    updated     timestamp,
    amount      integer   not null,
    description text,
    user_id     integer   not null
);

alter table calorie_tracker
    owner to quarkus;

