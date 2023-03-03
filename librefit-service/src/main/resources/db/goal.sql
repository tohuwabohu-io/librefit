create table goal
(
    id           bigint not null
        primary key,
    end_amount   real,
    end_date     timestamp,
    start_amount real,
    start_date   timestamp,
    user_id      bigint
);

alter table goal
    owner to librefit;
