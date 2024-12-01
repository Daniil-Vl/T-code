--liquibase formatted sql
--changeset Daniil-Vl:6

CREATE TABLE IF NOT EXISTS contests_problems
(
    contest_id BIGINT REFERENCES contest (id) ON DELETE CASCADE NOT NULL,
    problem_id BIGINT REFERENCES problem (id) ON DELETE CASCADE NOT NULL
);

-- ROLLBACK DROP TABLE contests_problems;