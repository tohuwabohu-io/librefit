drop table auth_session;

drop table goal;

create table calorie_tracker_entry (
  added date not null,
  amount real not null,
  sequence bigint not null,
  updated timestamp(6) without time zone,
  user_id uuid not null,
  category character varying(255) not null,
  description character varying(255),
  primary key (added, sequence, user_id)
);

create table weight_tracker_entry (
     added date not null,
     amount real not null,
     sequence bigint not null,
     updated timestamp(6) without time zone,
     user_id uuid not null,
     primary key (added, sequence, user_id)
);

