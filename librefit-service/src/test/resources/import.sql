insert into libre_user (id, email, password, role, activated) values
 ('1171b08c-7fb5-11ee-b962-0242ac120002', 'unit-test1@test.dev', '$2a$10$oXl/TR2nfJCux04Na./7b.Iu7oIOnhcu0aD9YeEq0YcFahlDc37fy', 'User', false);

insert into food_category (shortvalue, longvalue, visible)
values ('b', 'Breakfast', true),
       ('l', 'Lunch', true),
       ('d', 'Dinner', true),
       ('s', 'Snack', true),
       ('t', 'Treat', true),
       ('u', 'Unset', false);

-- dashboard test
insert into libre_user (id, email, password, role, name, avatar, activated) values ('2291b08c-7fb5-11ee-b962-0242ac120002', 'dashboard@test.dev', '<pwd>', 'User', 'Sly', '/assets/rambo-1.jpg', true);
insert into calorie_tracker_entry (user_id, added, sequence, category, amount) values ('2291b08c-7fb5-11ee-b962-0242ac120002', '2024-05-31', 1, 'l', 300);
insert into weight_tracker_entry (user_id, added, sequence, amount) values ('2291b08c-7fb5-11ee-b962-0242ac120002', '2024-05-31', 1, 80);
insert into calorie_target(user_id, added, sequence, start_date, end_date, target_calories) values ('2291b08c-7fb5-11ee-b962-0242ac120002', '2024-05-31', 1, '2024-01-01', '2024-12-31', 1900);
insert into weight_target(user_id, added, sequence, start_date, end_date, initial_weight, target_weight) values ('2291b08c-7fb5-11ee-b962-0242ac120002', '2024-05-01', 1, '2024-01-01', '2024-12-31', 90, 75);
