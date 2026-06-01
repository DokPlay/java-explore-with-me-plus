package ru.practicum.main.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Checks that each label in the email domain does not exceed 63 characters.
 */
public class EmailDomainLabelValidator implements ConstraintValidator<ValidEmailDomainLabel, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // NotBlank/@Email handle this
        }

        int atIndex = email.lastIndexOf('@');
        if (atIndex < 0 || atIndex == email.length() - 1) {
            return true; // Let @Email handle invalid format
        }

        String domain = email.substring(atIndex + 1);
        String[] labels = domain.split("\\.");

        for (String label : labels) {
            if (label.length() > 63) {
                return false;
            }
        }

        return true;
    }
}
