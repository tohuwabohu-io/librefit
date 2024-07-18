drop table auth_session;

create table calorie_target
(
    added            date   not null,
    end_date         date   not null,
    maximum_calories real   not null,
    start_date       date   not null,
    target_calories  real   not null,
    sequence         bigint not null,
    updated          timestamp(6) without time zone,
    user_id          uuid   not null,
    primary key (added, sequence, user_id)
);

create table weight_target
(
    added          date   not null,
    end_date       date   not null,
    initial_weight real   not null,
    start_date     date   not null,
    target_weight  real   not null,
    sequence       bigint not null,
    updated        timestamp(6) without time zone,
    user_id        uuid   not null,
    primary key (added, sequence, user_id)
);

alter table calorie_target
    owner to librefit;

alter table weight_target
    owner to libreift;

insert into calorie_target (user_id, added, sequence, start_date, end_date, target_calories, maximum_calories, updated)
    select user_id, added, sequence, start_date, end_date, target_calories, maximum_calories, updated from goal;

insert into weight_target (user_id, added, sequence, start_date, end_date, target_weight, initial_weight, updated)
    select user_id, added, sequence, start_date, end_date, target_weight, initial_weight, updated from goal;

drop table goal;