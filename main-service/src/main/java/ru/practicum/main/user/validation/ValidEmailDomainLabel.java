package ru.practicum.main.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that each domain label of an email is not longer than 63 characters.
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailDomainLabelValidator.class)
public @interface ValidEmailDomainLabel {

    String message() default "Email domain label exceeds 63 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
