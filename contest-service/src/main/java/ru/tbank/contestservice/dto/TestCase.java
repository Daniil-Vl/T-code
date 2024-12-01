package ru.tbank.contestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TestCase(
        @NotBlank
        @JsonProperty("input")
        String input,

        @NotBlank
        @JsonProperty("output")
        String output
) {
}
