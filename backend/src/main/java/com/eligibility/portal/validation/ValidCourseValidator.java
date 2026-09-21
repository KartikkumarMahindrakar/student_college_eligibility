package com.eligibility.portal.validation;

import com.eligibility.portal.catalog.Course;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/** Stateless - the catalog is a compile-time enum, so no Spring DI/DB lookup is needed. */
public class ValidCourseValidator implements ConstraintValidator<ValidCourse, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // absence is @NotBlank's concern
        }
        return Course.fromDisplayName(value).isPresent();
    }
}
