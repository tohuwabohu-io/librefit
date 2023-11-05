create extension pgcrypto;

create function encrypt_pwd()
    returns trigger AS $$
begin
    NEW.password := crypt(NEW.password, gen_salt('bf'));
    return NEW;
end;
$$ language plpgsql;

alter function encrypt_pwd() owner to librefit;

create table public.libre_user
(
    id         serial   primary key,
    email      varchar(255) not null
        constraint email_unique
            unique,
    last_login timestamp,
    name       varchar(255),
    password   varchar(255) not null,
    registered timestamp default CURRENT_TIMESTAMP,
    avatar     varchar(255)
);

alter table public.libre_user
    owner to librefit;

create trigger password_trigger
    before insert
    on public.libre_user
    for each row
execute procedure public.encrypt_pwd();
