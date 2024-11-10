package ru.tbank.submissionservice.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.tbank.submissionservice.enums.Language;
import ru.tbank.submissionservice.validation.annotations.LanguageConstraint;

import java.util.Arrays;

public class LanguageValidator implements ConstraintValidator<LanguageConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays
                .stream(Language.values())
                .anyMatch(
                        language -> language.getLanguageName()
                                .equals(value.toLowerCase())
                );
    }

}
