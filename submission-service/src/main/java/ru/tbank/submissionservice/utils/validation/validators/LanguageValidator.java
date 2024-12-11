package ru.tbank.submissionservice.utils.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.submissionservice.service.language.LanguageService;
import ru.tbank.submissionservice.utils.validation.annotations.LanguageConstraint;

@Component
@RequiredArgsConstructor
public class LanguageValidator implements ConstraintValidator<LanguageConstraint, String> {

    private final LanguageService languageService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return languageService
                .getLanguages()
                .stream()
                .anyMatch(lang -> lang.name().equals(value));
    }

}
