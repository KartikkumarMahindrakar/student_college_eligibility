package com.eligibility.portal.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Validates that a String is one of the 13 subjects in the {@link com.eligibility.portal.catalog.Subject} master list. */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSubjectNameValidator.class)
public @interface ValidSubjectName {

    String message() default "Subject name must be one of the predefined subjects";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
