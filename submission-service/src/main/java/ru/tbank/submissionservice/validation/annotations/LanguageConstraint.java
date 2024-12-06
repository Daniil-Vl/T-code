package ru.tbank.submissionservice.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tbank.submissionservice.validation.validators.LanguageValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LanguageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LanguageConstraint {
    String message() default "Invalid language";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
