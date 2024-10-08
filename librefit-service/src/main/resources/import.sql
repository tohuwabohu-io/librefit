-- default dev user with pwd 'test1'
insert into weight_tracker (added, sequence, user_id, amount)
values (CURRENT_DATE - 58, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 57, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 56, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 55, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 54, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 53, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 52, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 51, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 100),
       (CURRENT_DATE - 50, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 99),
       (CURRENT_DATE - 49, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 99),
       (CURRENT_DATE - 48, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 99),
       (CURRENT_DATE - 47, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 99),
       (CURRENT_DATE - 46, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 99),
       (CURRENT_DATE - 45, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 98),
       (CURRENT_DATE - 44, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 98),
       (CURRENT_DATE - 43, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 98),
       (CURRENT_DATE - 42, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 98),
       (CURRENT_DATE - 41, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 40, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 39, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 38, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 37, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 36, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 35, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 34, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 33, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 32, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 31, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97);

INSERT INTO public.libre_user (activated, last_login, registered, id, avatar, email, name, password, role) VALUES (true, null, NOW(), 'b21291e5-db51-4a52-a997-25f4021c0ac6', '/assets/images/avatars/dog-1.png', 'test1@test.dev', 'Arnie', '$2a$10$sFGAyHJY3jWxLjhkr7q8B.DdzYL5v4CqTDlW1iVQhPOrSN9UEqoBe', 'User');
INSERT INTO public.libre_user (activated, last_login, registered, id, avatar, email, name, password, role) VALUES (false, null, NOW(), '4dd0a829-1bfa-48cc-b13c-a80eb36b1e3f', '/assets/images/avatars/buffdude-1.png', 'test2@test.dev', 'Testuser 2', '$2a$10$gKye9AdO/HeGqcbF5Cg1eObW3kmz26d107xmzSEHAtBaB/dkDWoa2', 'User');
INSERT INTO public.libre_user (activated, last_login, registered, id, avatar, email, name, password, role) VALUES (false, null, NOW(), '8d45c705-feb3-4daf-80f0-dd89bbccd746', '/assets/images/avatars/lady-1.png', 'test3@test.dev', 'Testuser 3', '$2a$10$HooeSwYKozLREB36NVXWoumq5LH.gTh6KFJpuO8WnPPHyxBIsfea6', 'User');

insert into calorie_tracker (added, sequence, user_id, amount, category)
values (CURRENT_DATE - 58, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 58, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 270, 'l'),
       (CURRENT_DATE - 58, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'd'),
       (CURRENT_DATE - 58, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 518, 's'),
       (CURRENT_DATE - 57, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 57, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 57, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 500, 'd'),
       (CURRENT_DATE - 57, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 's'),
       (CURRENT_DATE - 56, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 56, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 56, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'd'),
       (CURRENT_DATE - 56, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 's'),
       (CURRENT_DATE - 55, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 55, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'l'),
       (CURRENT_DATE - 55, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 577, 'd'),
       (CURRENT_DATE - 55, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 's'),
       (CURRENT_DATE - 54, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 54, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 54, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 535, 'd'),
       (CURRENT_DATE - 54, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 142, 's'),
       (CURRENT_DATE - 53, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 53, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 375, 'l'),
       (CURRENT_DATE - 53, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'd'),
       (CURRENT_DATE - 53, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 320, 's'),
       (CURRENT_DATE - 52, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 52, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'l'),
       (CURRENT_DATE - 52, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1400, 'd'),
       (CURRENT_DATE - 52, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 51, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 51, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'l'),
       (CURRENT_DATE - 51, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'd'),
       (CURRENT_DATE - 51, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 890, 's'),
       (CURRENT_DATE - 50, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 50, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'l'),
       (CURRENT_DATE - 50, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 550, 'd'),
       (CURRENT_DATE - 50, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 49, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'b'),
       (CURRENT_DATE - 49, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 810, 'l'),
       (CURRENT_DATE - 49, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 346, 'd'),
       (CURRENT_DATE - 49, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 48, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 'b'),
       (CURRENT_DATE - 48, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'l'),
       (CURRENT_DATE - 48, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 850, 'd'),
       (CURRENT_DATE - 48, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 's'),
       (CURRENT_DATE - 47, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 47, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 47, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'd'),
       (CURRENT_DATE - 47, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 46, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 46, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'l'),
       (CURRENT_DATE - 46, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 627, 'd'),
       (CURRENT_DATE - 46, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 45, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 45, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 560, 'l'),
       (CURRENT_DATE - 45, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'd'),
       (CURRENT_DATE - 45, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 640, 's'),
       (CURRENT_DATE - 44, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 480, 'b'),
       (CURRENT_DATE - 44, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 560, 'l'),
       (CURRENT_DATE - 44, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'd'),
       (CURRENT_DATE - 44, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 43, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 43, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'l'),
       (CURRENT_DATE - 43, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 43, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 139, 's'),
       (CURRENT_DATE - 42, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 42, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 42, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 605, 'd'),
       (CURRENT_DATE - 42, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 41, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 41, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'l'),
       (CURRENT_DATE - 41, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 'd'),
       (CURRENT_DATE - 41, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 's'),
       (CURRENT_DATE - 40, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 376, 'b'),
       (CURRENT_DATE - 40, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'l'),
       (CURRENT_DATE - 40, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 40, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 's'),
       (CURRENT_DATE - 39, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 39, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'l'),
       (CURRENT_DATE - 39, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 810, 'd'),
       (CURRENT_DATE - 39, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 38, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'b'),
       (CURRENT_DATE - 38, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'l'),
       (CURRENT_DATE - 38, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'd'),
       (CURRENT_DATE - 38, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 's'),
       (CURRENT_DATE - 37, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 37, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 37, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 730, 'd'),
       (CURRENT_DATE - 37, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 36, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 36, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'l'),
       (CURRENT_DATE - 36, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'd'),
       (CURRENT_DATE - 36, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 285, 's'),
       (CURRENT_DATE - 35, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 35, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'l'),
       (CURRENT_DATE - 35, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1200, 'd'),
       (CURRENT_DATE - 35, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 760, 's'),
       (CURRENT_DATE - 34, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'b'),
       (CURRENT_DATE - 34, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'l'),
       (CURRENT_DATE - 34, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'd'),
       (CURRENT_DATE - 34, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 's'),
       (CURRENT_DATE - 33, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 33, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 'l'),
       (CURRENT_DATE - 33, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'd'),
       (CURRENT_DATE - 33, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 369, 's'),
       (CURRENT_DATE - 32, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 32, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 'l'),
       (CURRENT_DATE - 32, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 840, 'd'),
       (CURRENT_DATE - 32, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 160, 's'),
       (CURRENT_DATE - 31, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 31, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 31, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 660, 'd'),
       (CURRENT_DATE - 31, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 's');


insert into calorie_tracker (added, sequence, user_id, amount, category)
values (CURRENT_DATE - 30, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 0, 'b'),
       (CURRENT_DATE - 30, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 30, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1200, 'd'),
       (CURRENT_DATE - 29, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 29, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'l'),
       (CURRENT_DATE - 29, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'd'),
       (CURRENT_DATE - 29, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 180, 's'),
       (CURRENT_DATE - 28, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 28, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'l'),
       (CURRENT_DATE - 28, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1200, 'd'),
       (CURRENT_DATE - 27, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 27, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1200, 'l'),
       (CURRENT_DATE - 27, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 26, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 26, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 'l'),
       (CURRENT_DATE - 26, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 'd'),
       (CURRENT_DATE - 25, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 25, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'l'),
       (CURRENT_DATE - 25, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 643, 'd'),
       (CURRENT_DATE - 25, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 139, 's'),
       (CURRENT_DATE - 24, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 24, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 643, 'l'),
       (CURRENT_DATE - 24, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 820, 'd'),
       (CURRENT_DATE - 24, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 139, 's'),
       (CURRENT_DATE - 23, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 23, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1250, 'l'),
       (CURRENT_DATE - 23, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 350, 'd'),
       (CURRENT_DATE - 22, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 22, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'l'),
       (CURRENT_DATE - 22, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'd'),
       (CURRENT_DATE - 22, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 's'),
       (CURRENT_DATE - 21, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 300, 'b'),
       (CURRENT_DATE - 21, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'l'),
       (CURRENT_DATE - 21, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'd'),
       (CURRENT_DATE - 20, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 440, 'b'),
       (CURRENT_DATE - 20, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 20, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'd'),
       (CURRENT_DATE - 20, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 359, 's'),
       (CURRENT_DATE - 19, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 19, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 850, 'l'),
       (CURRENT_DATE - 19, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 300, 'd'),
       (CURRENT_DATE - 19, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 's'),
       (CURRENT_DATE - 18, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 18, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 850, 'l'),
       (CURRENT_DATE - 18, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 565, 'd'),
       (CURRENT_DATE - 18, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 300, 's'),
       (CURRENT_DATE - 17, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 17, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 17, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 17, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 284, 's'),
       (CURRENT_DATE - 16, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 16, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 'l'),
       (CURRENT_DATE - 16, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'd'),
       (CURRENT_DATE - 15, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 15, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1200, 'l'),
       (CURRENT_DATE - 15, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 'd'),
       (CURRENT_DATE - 14, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 14, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 'l'),
       (CURRENT_DATE - 14, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 2400, 'd'),
       (CURRENT_DATE - 13, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 0, 'b'),
       (CURRENT_DATE - 13, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 13, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 13, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 384, 's'),
       (CURRENT_DATE - 12, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 383, 'b'),
       (CURRENT_DATE - 12, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'l'),
       (CURRENT_DATE - 12, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 11, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 0, 'b'),
       (CURRENT_DATE - 11, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 11, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 562, 'd'),
       (CURRENT_DATE - 11, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 387, 's'),
       (CURRENT_DATE - 10, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 10, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 10, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd'),
       (CURRENT_DATE - 10, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 500, 's'),
       (CURRENT_DATE - 9, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 9, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 9, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'd'),
       (CURRENT_DATE - 8, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 8, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1200, 'l'),
       (CURRENT_DATE - 8, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 600, 'd'),
       (CURRENT_DATE - 7, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 0, 'b'),
       (CURRENT_DATE - 7, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 7, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'd'),
       (CURRENT_DATE - 6, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 450, 'b'),
       (CURRENT_DATE - 6, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'l'),
       (CURRENT_DATE - 6, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'd'),
       (CURRENT_DATE - 5, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 5, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'l'),
       (CURRENT_DATE - 5, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'd'),
       (CURRENT_DATE - 4, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 4, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 950, 'l'),
       (CURRENT_DATE - 4, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'd'),
       (CURRENT_DATE - 3, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 3, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 360, 'l'),
       (CURRENT_DATE - 3, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 750, 'd'),
       (CURRENT_DATE - 3, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 700, 's'),
       (CURRENT_DATE - 2, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 240, 'b'),
       (CURRENT_DATE - 2, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 650, 'l'),
       (CURRENT_DATE - 2, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 400, 'd'),
       (CURRENT_DATE - 2, 4, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 's'),
       (CURRENT_DATE - 1, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 500, 'b'),
       (CURRENT_DATE - 1, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 500, 'l'),
       (CURRENT_DATE - 1, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 900, 'd'),
       (CURRENT_DATE, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 0, 'b'),
       (CURRENT_DATE, 2, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 1000, 'l'),
       (CURRENT_DATE, 3, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 800, 'd');

insert into weight_tracker (added, sequence, user_id, amount)
values (CURRENT_DATE - 30, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 29, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 28, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 97),
       (CURRENT_DATE - 27, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 96),
       (CURRENT_DATE - 26, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 96),
       (CURRENT_DATE - 25, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 96),
       (CURRENT_DATE - 24, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 96),
       (CURRENT_DATE - 23, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 96),
       (CURRENT_DATE - 22, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 96),
       (CURRENT_DATE - 21, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 95),
       (CURRENT_DATE - 20, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 95),
       (CURRENT_DATE - 19, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 95),
       (CURRENT_DATE - 18, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 95),
       (CURRENT_DATE - 17, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 95),
       (CURRENT_DATE - 16, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 15, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 14, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 13, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 12, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 11, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 10, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 9, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 8, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 7, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 6, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 5, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 4, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 3, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 94),
       (CURRENT_DATE - 2, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 93),
       (CURRENT_DATE - 1, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 93),
       (CURRENT_DATE, 1, 'b21291e5-db51-4a52-a997-25f4021c0ac6', 93);

insert into calorie_target (user_id, added, sequence, start_date, end_date, target_calories, maximum_calories )
values ('b21291e5-db51-4a52-a997-25f4021c0ac6', CURRENT_DATE - 58, 1, CURRENT_DATE - 58, CURRENT_DATE + 307, 1849, 2398);

insert into weight_target (user_id, added, sequence, start_date, end_date, initial_weight, target_weight )
values ('b21291e5-db51-4a52-a997-25f4021c0ac6', CURRENT_DATE - 58, 1, CURRENT_DATE - 58, CURRENT_DATE + 307, 104, 77);

insert into food_category (shortvalue, longvalue, visible)
values ('b', 'Breakfast', true),
       ('l', 'Lunch', true),
       ('d', 'Dinner', true),
       ('s', 'Snack', true),
       ('t', 'Treat', true),
       ('u', 'Unset', false);
