create table public.calorie_tracker_entry
(
    added       date default CURRENT_TIMESTAMP not null,
    id          bigint                         not null,
    user_id     bigint                         not null
        constraint fk_user
            references public.libre_user,
    amount      real                           not null,
    category    varchar(255)                   not null,
    description varchar(255),
    updated     timestamp,
    primary key (added, id, user_id)
);

alter table public.calorie_tracker_entry
    owner to librefit;

insert into calorie_tracker_entry (added, user_id, id, amount, description, updated, category)
    values ('2022-03-19', 1, 1, 240, null, 1, 'b'),
           ('2022-03-19', 1, 2, 600, null, 1, 'l'),
           ('2022-03-19', 1, 3, 400, null, 1, 'd'),
           ('2022-03-19', 1, 4, 400, null, 1, 's'),
           ('2022-03-20', 1, 1, 600, null, 1, 'b'),
           ('2022-03-20', 1, 2, 750, null, 1, 'l'),
           ('2022-03-20', 1, 3, 400, null, 1, 'd'),
           ('2022-03-21', 1, 1, 900, null, 1, 'l'),
           ('2022-03-21', 1, 2, 800, null, 1, 'd');
