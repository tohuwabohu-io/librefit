insert into libre_user (id, email, password, role, activated) values
 ('1171b08c-7fb5-11ee-b962-0242ac120002', 'unit-test1@test.dev', '$2a$10$oXl/TR2nfJCux04Na./7b.Iu7oIOnhcu0aD9YeEq0YcFahlDc37fy', 'User', false);

insert into food_category (shortvalue, longvalue, visible)
values ('b', 'Breakfast', true),
       ('l', 'Lunch', true),
       ('d', 'Dinner', true),
       ('s', 'Snack', true),
       ('t', 'Treat', true),
       ('u', 'Unset', false);