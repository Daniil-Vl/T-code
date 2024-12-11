--liquibase formatted sql
--changeset Daniil-Vl:4

CREATE TABLE IF NOT EXISTS test_case
(
    id         BIGSERIAL PRIMARY KEY,
    number     INT                                              NOT NULL,
    problem_id BIGINT REFERENCES problem (id) ON DELETE CASCADE NOT NULL
);

-- ROLLBACK DROP TABLE test_case;