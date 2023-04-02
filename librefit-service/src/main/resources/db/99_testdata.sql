insert into libre_user (id, email, name, password, registered)
values  (1, 'test1@test.dev', 'test1', 'test1', '2023-03-01'),
        (2, 'test2@test.dev', 'Johnny Cool', 'test2', '2023-03-01'),
        (3, 'test3@test.dev', 'Hugh Jazz', 'test3', '2023-03-01'),
        (4, 'test4@test.dev', 'Donald Duck', 'test4', '2023-03-01'),
        (5, 'test5@test.dev', 'Gustav Gans', 'test5', '2023-03-01'),
        (6, 'test6@test.dev', 'Darkwing Duck', 'test6', '2023-03-01');

insert into calorie_tracker_entry (added, user_id, id, amount, description, updated, category)
values ('2022-03-19', 1, 1, 240, null, null, 'b'),
       ('2022-03-19', 1, 2, 600, null, null, 'l'),
       ('2022-03-19', 1, 3, 400, null, null, 'd'),
       ('2022-03-19', 1, 4, 400, null, null, 's'),
       ('2022-03-20', 1, 1, 600, null, null, 'b'),
       ('2022-03-20', 1, 2, 750, null, null, 'l'),
       ('2022-03-20', 1, 3, 400, null, null, 'd'),
       ('2022-03-21', 1, 1, 900, null, null, 'l'),
       ('2022-03-21', 1, 2, 800, null, null, 'd');
