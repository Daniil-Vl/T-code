--liquibase formatted sql
--changeset Daniil-Vl:1

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    email    TEXT
);

-- ROLLBACK DROP TABLE users;