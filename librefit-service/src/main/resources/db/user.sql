create table "user"
(
    id         integer,
    name       text not null,
    password   text not null,
    email      text not null,
    registered timestamp,
    last_login timestamp
);

alter table "user"
    owner to quarkus;

