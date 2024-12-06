--liquibase formatted sql
--changeset Daniil-Vl:3

CREATE TABLE IF NOT EXISTS problem
(
    id                    BIGSERIAL PRIMARY KEY,
    title                 VARCHAR(255)                                                NOT NULL,
    description           TEXT                                                        NOT NULL,
    owner_id              BIGINT REFERENCES users (id) ON DELETE CASCADE              NOT NULL
);

-- ROLLBACK DROP TABLE problem;