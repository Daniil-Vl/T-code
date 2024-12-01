--liquibase formatted sql
--changeset Daniil-Vl:2

CREATE TABLE IF NOT EXISTS contest
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255)                                   NOT NULL,
    start_time TIMESTAMPTZ                                    NOT NULL,
    end_time   TIMESTAMPTZ                                    NOT NULL,
    owner_id   BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL
);

-- ROLLBACK DROP TABLE contest;