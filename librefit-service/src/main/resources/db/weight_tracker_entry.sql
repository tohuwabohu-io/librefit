create table weight_tracker_entry
(
    id      bigint not null
        primary key,
    added   timestamp,
    amount  real,
    updated timestamp,
    user_id bigint
);

alter table weight_tracker_entry
    owner to librefit;

