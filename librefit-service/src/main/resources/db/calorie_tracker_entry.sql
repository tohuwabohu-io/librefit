create table calorie_tracker_entry
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

alter table calorie_tracker_entry
    owner to librefit;

insert into calorie_tracker_entry (id, added, amount, description, updated, user_id, category)
    values (1, '2022-03-19', 300, null, null, 1, 'b'),
           (2, '2022-03-19', 500, null, null, 1, 'l'),
           (3, '2022-03-19', 400, null, null, 1, 'd');
