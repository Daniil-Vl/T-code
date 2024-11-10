package ru.tbank.submissionservice.enums;

import lombok.Getter;

@Getter
public enum Language {
    PYTHON(71, "python"),
    JAVA(62, "java"),
    CPP(54, "cpp"),
    RUBY(72, "ruby"),
    BASH(46, "bash");

    private final int languageId;
    private final String languageName;

    Language(int languageId, String languageName) {
        this.languageId = languageId;
        this.languageName = languageName;
    }
}
