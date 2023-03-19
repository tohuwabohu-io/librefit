create table public.calorie_tracker_entry
(
    id          bigint not null
        primary key,
    added       timestamp,
    amount      real,
    description varchar(255),
    updated     timestamp,
    user_id     bigint,
    category    varchar(255)
);

alter table public.calorie_tracker_entry
    owner to librefit;

