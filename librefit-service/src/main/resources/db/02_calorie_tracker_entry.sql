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
