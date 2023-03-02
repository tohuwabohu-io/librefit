create table libre_user
(
    id         bigint not null
        primary key,
    email      varchar(255),
    last_login timestamp,
    name       varchar(255),
    password   varchar(255),
    registered timestamp
);

alter table libre_user
    owner to librefit;
