insert into libre_user (id, email, password, role, activated) values
 ('1171b08c-7fb5-11ee-b962-0242ac120002', 'unit-test1@test.dev', '$2a$10$oXl/TR2nfJCux04Na./7b.Iu7oIOnhcu0aD9YeEq0YcFahlDc37fy', 'User', false);

insert into account_activation values (CURRENT_DATE, 1, CURRENT_DATE - 14, '1171b08c-7fb5-11ee-b962-0242ac120002', '89a4e017-8481-3658-b9e6-3930cd79b077');
insert into account_activation values (CURRENT_DATE, 2, CURRENT_DATE + 14, '1171b08c-7fb5-11ee-b962-0242ac120002', '89a4e017-8781-3658-b9e6-3930cd79b078');

insert into food_category (shortvalue, longvalue, visible)
values ('b', 'Breakfast', true),
       ('l', 'Lunch', true),
       ('d', 'Dinner', true),
       ('s', 'Snack', true),
       ('t', 'Treat', true),
       ('u', 'Unset', false);