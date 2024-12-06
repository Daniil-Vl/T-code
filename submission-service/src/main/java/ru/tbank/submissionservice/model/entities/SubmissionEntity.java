package ru.tbank.submissionservice.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;

import java.time.OffsetDateTime;

@Entity
@Table(name = "submission")
@IdClass(value = SubmissionId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Id
    @Column(name = "contest_id", nullable = false)
    private long contestId;

    @Id
    @Column(name = "problem_id", nullable = false)
    private long problemId;

    @Column(name = "code_key", nullable = false)
    private String codeKey;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Setter
    @Column(name = "status", nullable = false)
    private String status;

    @Setter
    @Column(name = "score", nullable = false)
    private int score;

    @Setter
    @Column(name = "execution_time_ms", nullable = false)
    private int executionTimeMs;

    @Setter
    @Column(name = "memory_used_kb", nullable = false)
    private int memoryUsedKb;

    @Override
    public String toString() {
        return "SubmissionEntity{" +
                "userId=" + userId +
                ", contestId=" + contestId +
                ", problemId=" + problemId +
                ", codeKey='" + codeKey + '\'' +
                ", submittedAt=" + submittedAt +
                ", status='" + status + '\'' +
                ", score=" + score +
                ", executionTimeMs=" + executionTimeMs +
                ", memoryUsedKb=" + memoryUsedKb +
                '}';
    }

}
