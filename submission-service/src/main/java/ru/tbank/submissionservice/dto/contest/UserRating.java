package ru.tbank.submissionservice.dto.contest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public record UserRating(
        @JsonProperty("contest_id")
        long contestId,

        @JsonProperty("rating")
        List<User> userRating
) {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User implements Comparable<User> {
        @JsonProperty("user_id")
        private long userId;

        @JsonProperty("solved_problem_number")
        private int solvedProblems;

        @JsonProperty("total_score")
        private int totalScore;

        public User(long userId) {
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof User user)) {
                return false;
            }

            return userId == user.userId && solvedProblems == user.solvedProblems && totalScore == user.totalScore;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, solvedProblems, totalScore);
        }

        @Override
        public int compareTo(User other) {
            if (this.solvedProblems != other.solvedProblems) {
                return other.solvedProblems - this.solvedProblems;
            }
            return other.totalScore - this.totalScore;
        }
    }

}
