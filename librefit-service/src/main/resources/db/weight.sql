create table weight
(
    user_id integer   not null,
    amount  real      not null,
    unit    text default 'kg'::text,
    added   timestamp not null
);

alter table weight
    owner to quarkus;

