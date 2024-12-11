package ru.tbank.contestservice.utils.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.utils.validation.annotations.LanguageConstraint;

@Component
@RequiredArgsConstructor
public class LanguageValidator implements ConstraintValidator<LanguageConstraint, String> {

    private final SubmissionServiceClient submissionServiceClient;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return submissionServiceClient.getAvailableLanguages().contains(value);
    }
}
