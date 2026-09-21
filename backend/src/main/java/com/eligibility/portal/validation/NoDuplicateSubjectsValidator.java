package com.eligibility.portal.validation;

import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.request.SubjectMarkRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoDuplicateSubjectsValidator implements ConstraintValidator<NoDuplicateSubjects, CheckEligibilityRequest> {

    @Override
    public boolean isValid(CheckEligibilityRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getSubjectMarks() == null) {
            return true; // absence/size is @NotNull/@Size's concern
        }

        Set<String> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (SubjectMarkRequest mark : request.getSubjectMarks()) {
            if (mark == null || mark.getSubjectName() == null || mark.getSubjectName().isBlank()) {
                continue; // field-level @NotBlank's concern
            }
            String normalized = mark.getSubjectName().trim().toLowerCase();
            if (!seen.add(normalized)) {
                duplicates.add(mark.getSubjectName());
            }
        }

        if (duplicates.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        "Duplicate subject(s) selected: " + String.join(", ", duplicates)
                                + ". Each of the 6 subjects must be distinct.")
                .addPropertyNode("subjectMarks")
                .addConstraintViolation();
        return false;
    }
}
