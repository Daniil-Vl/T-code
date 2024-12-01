--liquibase formatted sql
--changeset Daniil-Vl:5

CREATE TABLE IF NOT EXISTS users_contests
(
    user_id    BIGINT REFERENCES users (id) ON DELETE CASCADE   NOT NULL,
    contest_id BIGINT REFERENCES contest (id) ON DELETE CASCADE NOT NULL
);

-- ROLLBACK DROP TABLE users_contests;