package com.eligibility.portal.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Class-level: the 6 subject-mark entries on the annotated request must name 6 distinct subjects. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDuplicateSubjectsValidator.class)
public @interface NoDuplicateSubjects {

    String message() default "Subjects must be distinct";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
