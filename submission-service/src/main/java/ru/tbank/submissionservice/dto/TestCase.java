package ru.tbank.submissionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestCase {
    private String input;
    private String output;

    @Override
    public String toString() {
        return "TestCase [input=" + input + ", output=" + output + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestCase testCase)) {
            return false;
        }
        return Objects.equals(input, testCase.getInput()) && Objects.equals(output, testCase.getOutput());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInput(), getOutput());
    }
}
