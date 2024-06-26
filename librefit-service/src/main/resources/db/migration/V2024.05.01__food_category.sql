create table food_category
(
    longvalue  varchar(255) not null,
    shortvalue varchar(255) not null
        primary key,
    visible boolean not null
);

alter table food_category
    owner to librefit;

insert into food_category (shortvalue, longvalue, visible)
    values ('b', 'Breakfast', true),
           ('l', 'Lunch', true),
           ('d', 'Dinner', true),
           ('s', 'Snack', true),
           ('t', 'Treat', true),
           ('u', 'Unset', false);

