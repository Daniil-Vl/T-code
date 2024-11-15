package ru.tbank.submissionservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCase {
    private String input;
    private String output;

    @Override
    public String toString() {
        return "TestCase [input=" + input + ", output=" + output + "]";
    }
}
