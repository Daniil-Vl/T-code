package ru.tbank.contestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record TestCase(
        @NotNull
        @JsonProperty("input")
        String input,

        @NotNull
        @JsonProperty("output")
        String output
) {
}
