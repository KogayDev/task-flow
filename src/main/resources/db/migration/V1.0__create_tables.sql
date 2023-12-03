CREATE TABLE IF NOT EXISTS users
(
    id         SERIAL PRIMARY KEY,
    birth_date DATE,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    password   VARCHAR(255),
    role       VARCHAR(255) CHECK (role IN ('USER', 'ADMIN')),
    username   VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS task
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    status      VARCHAR(255) CHECK (status IN ('NEW', 'IN_PROGRESS', 'COMPLETED')),
    owner_id    INTEGER      NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users_task
(
    id              SERIAL PRIMARY KEY,
    assignment_date TIMESTAMP(6) NOT NULL,
    user_id         INTEGER      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    task_id         INTEGER      NOT NULL REFERENCES task (id) ON DELETE CASCADE,
    unique (user_id, task_id)
);
