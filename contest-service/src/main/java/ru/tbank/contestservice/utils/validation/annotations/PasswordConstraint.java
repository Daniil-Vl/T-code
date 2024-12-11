package ru.tbank.contestservice.utils.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tbank.contestservice.utils.validation.validators.PasswordConstraintValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {

    String message() default "Weak password, password must have 6 or more symbols and must contain letters, digits and one of special characters - !@#$%^&*()_+";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
