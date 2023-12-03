-- Data generation for the "users" table
INSERT INTO users(id, username, password, first_name, last_name, role, birth_date)
VALUES
    (1, 'ivan_ivanov@mail.ru', '{noop}123', 'Ivan', 'Ivanov', 'ADMIN', '1990-01-15'),
    (2, 'maria_petrova@mail.ru', '{noop}123', 'Maria', 'Petrova', 'USER', '1985-03-22'),
    (3, 'ivan_sidorov@mail.ru', '{noop}123', 'Ivan', 'Sidorov', 'USER', '1992-07-10'),
    (4, 'katya_kozlova@mail.ru', '{noop}123', 'Ekaterina', 'Kozlova', 'ADMIN', '1988-12-05'),
    (5, 'dmitry_smirnov@mail.ru', '{noop}123', 'Dmitry', 'Smirnov', 'USER', '1995-09-28'),
    (6, 'olga_ivanova@mail.ru', '{noop}123', 'Olga', 'Ivanova', 'USER', '1993-04-17'),
    (7, 'sergei_kuznetsov@mail.ru', '{noop}123', 'Sergei', 'Smirnov', 'ADMIN', '1987-06-30'),
    (8, 'anna_fedorova@mail.ru', '{noop}123', 'Anna', 'Fedorova', 'USER', '1994-11-12'),
    (9, 'test@gmail.com', '{noop}123', 'Test', 'Testovich', 'ADMIN', '1991-02-19'),
    (10, 'elena_kovaleva@mail.ru', '{noop}123', 'Elena', 'Kovaleva', 'ADMIN', '1986-08-08');

-- Set sequence for the "users" table
SELECT SETVAL('users_id_seq', (SELECT max(id) FROM users));

-- Data generation for the "task" table
INSERT INTO task(id, name, description, status, owner_id)
VALUES
    (1, 'Become a Java Developer', 'Develop a web application for the client', 'NEW', 1),
    (2, 'Create a Business Plan', 'Compose a business plan for a new project', 'IN_PROGRESS', 2),
    (3, 'Product Testing', 'Conduct testing of the latest product version', 'COMPLETED', 3),
    (4, 'Prototype Assembly', 'Assemble a device prototype for the client', 'NEW', 4),
    (5, 'Database Optimization', 'Optimize the database', 'IN_PROGRESS', 5),
    (6, 'Create a Marketing Campaign', 'Develop a marketing strategy', 'COMPLETED', 6),
    (7, 'Development of New Features', 'Add new functionality to the application', 'NEW', 7),
    (8, 'Staff Training', 'Conduct training for new staff', 'IN_PROGRESS', 8),
    (9, 'Market Analysis', 'Conduct market and competitor analysis', 'COMPLETED', 9),
    (10, 'Technology Research', 'Conduct research on new technologies', 'NEW', 2);

-- Set sequence for the "task" table
SELECT SETVAL('task_id_seq', (SELECT max(id) FROM task));

-- Data generation for the "users_task" table
INSERT INTO users_task(id, user_id, task_id, assignment_date)
VALUES
    (1, 1, 1, '2023-10-02'),
    (2, 2, 2, '2023-10-02'),
    (3, 3, 3, '2023-10-02'),
    (4, 4, 4, '2023-10-02'),
    (5, 5, 5, '2023-10-02'),
    (6, 6, 6, '2023-10-02'),
    (7, 7, 7, '2023-10-02'),
    (8, 8, 8, '2023-10-02'),
    (9, 9, 9, '2023-10-02'),
    (10, 10, 10, '2023-10-02'),
    (11, 1, 2, '2023-10-02'),
    (12, 2, 3, '2023-10-02'),
    (13, 3, 4, '2023-10-02'),
    (14, 4, 5, '2023-10-02'),
    (15, 5, 6, '2023-10-02'),
    (16, 6, 7, '2023-10-02'),
    (17, 7, 8, '2023-10-02'),
    (18, 8, 9, '2023-10-02'),
    (19, 9, 10, '2023-10-02'),
    (20, 10, 1, '2023-10-02');

-- Set sequence for the "users_task" table
SELECT SETVAL('users_task_id_seq', (SELECT max(id) FROM users_task));