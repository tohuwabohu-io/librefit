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

insert into libre_user (id, email, name, password, registered) values (1, 'test1@test.dev', 'test1', 'test1', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (2, 'test2@test.dev', 'Johnny Cool', 'test2', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (3, 'test3@test.dev', 'Hugh Jazz', 'test3', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (4, 'test4@test.dev', 'Donald Duck', 'test4', '2023-03-01');