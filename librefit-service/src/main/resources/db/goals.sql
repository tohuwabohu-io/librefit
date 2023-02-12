create table goals
(
    user_id      integer   not null,
    start_date   timestamp not null,
    end_date     timestamp not null,
    start_amount real      not null,
    end_amount   real      not null
);

alter table goals
    owner to quarkus;

