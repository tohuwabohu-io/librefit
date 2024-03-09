create table account_activation (
        added date not null,
        sequence bigint not null,
        user_id uuid not null,
        activation_id varchar(255) not null unique,
        valid_to timestamp(6) not null,
        primary key (added, sequence, user_id)
    );

create table auth_session (
    added date not null,
    sequence bigint not null,
    user_id uuid not null,
    expires_at timestamp(6),
    refresh_token varchar(255) not null unique,
    primary key (added, sequence, user_id)
);

create table calorie_tracker_entry (
    added date not null,
    sequence bigint not null,
    user_id uuid not null,
    amount float4 not null check (amount>=0),
    category varchar(255) not null,
    description varchar(255),
    updated timestamp(6),
    primary key (added, sequence, user_id)
);

create table goal (
    added date not null,
    sequence bigint not null,
    user_id uuid not null,
    end_date date not null,
    initial_weight float4 not null check (initial_weight>=0),
    maximum_calories float4 check (maximum_calories>=0),
    start_date date not null,
    target_calories float4 check (target_calories>=0),
    target_weight float4 not null check (target_weight>=0),
    updated timestamp(6),
    primary key (added, sequence, user_id)
);

create table libre_user (
    id uuid not null,
    activated boolean not null,
    avatar varchar(255),
    email varchar(255) not null unique,
    last_login timestamp(6),
    name varchar(255),
    password varchar(255) not null,
    registered timestamp(6),
    role varchar(255) not null,
    primary key (id)
);

create table weight_tracker_entry (
    added date not null,
    sequence bigint not null,
    user_id uuid not null,
    amount float4 not null check (amount>=0),
    updated timestamp(6),
    primary key (added, sequence, user_id)
);