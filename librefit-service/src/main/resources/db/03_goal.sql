create table public.goal
(
    added        date default CURRENT_TIMESTAMP not null,
    id           bigint                         not null,
    user_id      bigint                         not null
        constraint fk_user
            references public.libre_user,
    end_amount   real,
    end_date     date,
    start_amount real,
    start_date   date,
    updated timestamp,
    primary key (added, id, user_id)
);

alter table public.goal
    owner to librefit;
