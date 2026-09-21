package com.eligibility.portal.validation;

import com.eligibility.portal.catalog.Subject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidSubjectNameValidator implements ConstraintValidator<ValidSubjectName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // absence is @NotBlank's concern
        }
        return Subject.fromDisplayName(value).isPresent();
    }
}
