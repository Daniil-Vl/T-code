package ru.tbank.contestservice.dto.problem;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.model.entities.ProblemEntity;

import java.util.ArrayList;
import java.util.List;

public record ProblemDTO(
        @NotBlank(message = "title cannot be blank")
        @JsonProperty("title")
        String title,

        @NotBlank
        @JsonProperty("description")
        String description,

        @JsonProperty("test_cases")
        List<TestCase> testCases
) {
    public static ProblemDTO fromEntity(ProblemEntity problemEntity) {
        return new ProblemDTO(
                problemEntity.getTitle(),
                problemEntity.getDescription(),
                new ArrayList<>()
        );
    }
}
