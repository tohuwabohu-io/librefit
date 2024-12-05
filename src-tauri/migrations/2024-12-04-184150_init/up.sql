create table calorie_tracker
(
    id          integer not null primary key,
    added       text    not null,
    amount      integer    not null,
    category    text    not null,
    description text
);

create table weight_tracker
(
    id     integer not null primary key,
    added  text    not null,
    amount real    not null
);

create table food_category
(
    longvalue  varchar(255) not null,
    shortvalue varchar(255) not null primary key
);

insert into food_category (shortvalue, longvalue)
values ('b', 'Breakfast'),
       ('l', 'Lunch'),
       ('d', 'Dinner'),
       ('s', 'Snack'),
       ('t', 'Treat'),
       ('u', 'Unset');

create table calorie_target
(
    id               integer not null primary key,
    added            text    not null,
    end_date         text    not null,
    maximum_calories integer not null,
    start_date       text    not null,
    target_calories  integer not null
);

create table weight_target
(
    id             integer not null primary key,
    added          text    not null,
    end_date       text    not null,
    initial_weight real    not null,
    start_date     text    not null,
    target_weight  real    not null
);
