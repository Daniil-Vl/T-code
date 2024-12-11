package ru.tbank.submissionservice.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, SubmissionId>, JpaSpecificationExecutor<SubmissionEntity> {

    @Modifying
    @Query(
            value = """
                    UPDATE submission
                    SET status = :status,
                        score = :score,
                        execution_time_ms = :execution_time_ms,
                        memory_used_kb = :memory_used_kb
                    WHERE user_id = :user_id
                          AND contest_id = :contest_id
                          AND problem_id = :problem_id
                    """,
            nativeQuery = true
    )
    void updateSubmission(
            @Param("user_id") long userId,
            @Param("contest_id") long contestId,
            @Param("problem_id") long problemId,
            @Param("status") String status,
            @Param("score") int score,
            @Param("execution_time_ms") int executionTime,
            @Param("memory_used_kb") int memoryUsedKb
    );

    List<SubmissionEntity> getAllByContestId(long contestId);

}
