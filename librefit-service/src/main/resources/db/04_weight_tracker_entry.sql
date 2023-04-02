create table public.weight_tracker_entry
(
    added   date default CURRENT_TIMESTAMP not null,
    id      bigint                         not null,
    user_id bigint                         not null
        constraint fk_user
            references public.libre_user,
    amount  real,
    updated timestamp,
    primary key (added, id, user_id)
);

alter table public.weight_tracker_entry
    owner to librefit;

