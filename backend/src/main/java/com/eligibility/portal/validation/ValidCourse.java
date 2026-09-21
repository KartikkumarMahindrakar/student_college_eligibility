package com.eligibility.portal.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Validates that a String is one of the course names in the {@link com.eligibility.portal.catalog.Course} catalog. */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCourseValidator.class)
public @interface ValidCourse {

    String message() default "Desired course must be one of the predefined courses";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
