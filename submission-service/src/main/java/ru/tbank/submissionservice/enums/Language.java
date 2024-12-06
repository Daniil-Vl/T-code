package ru.tbank.submissionservice.enums;

public record Language(
        int id,
        String name
) {
    public static String getNameWithoutCompiler(String languageName) {
        int bracketIndex = languageName.lastIndexOf('(');

        if (bracketIndex == -1) {
            return languageName;
        }

        return languageName.substring(0, bracketIndex - 1);
    }
}
