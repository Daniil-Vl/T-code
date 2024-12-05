--liquibase formatted sql
--changeset Daniil-Vl:1

CREATE TABLE IF NOT EXISTS submission
(
    user_id           BIGINT      NOT NULL,
    contest_id        BIGINT      NOT NULL,
    problem_id        BIGINT      NOT NULL,
    code_key          TEXT        NOT NULL,
    submitted_at      TIMESTAMPTZ NOT NULL,
    status            TEXT        NOT NULL,
    score             INT         NOT NULL,
    execution_time_ms INT         NOT NULL,
    memory_used_kb    INT         NOT NULL,

    PRIMARY KEY (user_id, contest_id, problem_id)
);

-- ROLLBACK DROP TABLE submission;