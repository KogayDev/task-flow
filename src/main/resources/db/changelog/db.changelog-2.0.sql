--liquibase formatted sql

--changeset kogay:1
create table revision
(
    id        serial not null,
    timestamp bigint,
    username  varchar(255),
    primary key (id)
);

--changeset kogay:2
create table task_aud
(
    id          integer not null,
    rev         integer not null,
    revtype     smallint,
    description varchar(255),
    name        varchar(255),
    status      varchar(255) check (status in ('NEW', 'IN_PROGRESS', 'COMPLETED')),
    owner_id    integer,
    primary key (rev, id)
);

--changeset kogay:3
create table users_aud
(
    id         integer not null,
    rev        integer not null,
    revtype    smallint,
    birth_date date,
    first_name varchar(255),
    last_name  varchar(255),
    password   varchar(255),
    role       varchar(255) check (role in ('USER', 'ADMIN')),
    username   varchar(255),
    primary key (rev, id)
);

--changeset kogay:4
create table users_task_aud
(
    id              integer not null,
    rev             integer not null,
    revtype         smallint,
    assignment_date timestamp(6),
    task_id         integer,
    user_id         integer,
    primary key (rev, id)
)