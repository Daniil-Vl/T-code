package ru.tbank.contestservice.utils.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tbank.contestservice.utils.validation.validators.LanguageValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = LanguageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LanguageConstraint {
    String message() default "Invalid language";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
