package ru.tbank.submissionservice.model.entities.id;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SubmissionId implements Serializable {

    private long userId;
    private long contestId;
    private long problemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubmissionId that)) return false;
        return userId == that.userId && contestId == that.contestId && problemId == that.problemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, contestId, problemId);
    }

    @Override
    public String toString() {
        return "SubmissionId{" +
                "userId=" + userId +
                ", contestId=" + contestId +
                ", problemId=" + problemId +
                '}';
    }
}
