create extension pgcrypto;

create table libre_user
(
    id         bigint not null
        primary key,
    email      varchar(255) unique not null,
    last_login timestamp,
    name       varchar(255),
    password   varchar(255) not null,
    registered timestamp
);

alter table libre_user
    owner to librefit;

create function encrypt_pwd()
    returns trigger AS $$
begin
    NEW.password := crypt(NEW.password, gen_salt('bf'));
    return NEW;
end;
$$ language plpgsql;

alter function encrypt_pwd() owner to librefit;

create trigger password_trigger before insert on libre_user for each row execute procedure encrypt_pwd();

insert into libre_user (id, email, name, password, registered) values (1, 'test1@test.dev', 'test1', 'test1', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (2, 'test2@test.dev', 'Johnny Cool', 'test2', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (3, 'test3@test.dev', 'Hugh Jazz', 'test3', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (4, 'test4@test.dev', 'Donald Duck', 'test4', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (5, 'test5@test.dev', 'Gustav Gans', 'test5', '2023-03-01');
insert into libre_user (id, email, name, password, registered) values (6, 'test6@test.dev', 'Darkwing Duck', 'test6', '2023-03-01');