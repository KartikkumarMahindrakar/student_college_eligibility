package com.eligibility.portal.repository;

import com.eligibility.portal.entity.EligibilityResult;
import com.eligibility.portal.entity.StudentRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;

/** Composable filter predicates for the /students/history search/filter query params. */
public final class StudentRequestSpecifications {

    private StudentRequestSpecifications() {
    }

    public static Specification<StudentRequest> hasStudentName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("studentName")), "%" + name.trim().toLowerCase() + "%");
        };
    }

    public static Specification<StudentRequest> hasCourse(String course) {
        return (root, query, cb) -> {
            if (course == null || course.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("desiredCourse"), course);
        };
    }

    public static Specification<StudentRequest> hasEligibilityStatus(Boolean eligible) {
        return (root, query, cb) -> {
            if (eligible == null) {
                return cb.conjunction();
            }
            Join<StudentRequest, EligibilityResult> join = root.join("eligibilityResult", JoinType.INNER);
            return cb.equal(join.get("eligible"), eligible);
        };
    }

    public static Specification<StudentRequest> submittedBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("submittedAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("submittedAt"), from);
            }
            if (to != null) {
                return cb.lessThanOrEqualTo(root.get("submittedAt"), to);
            }
            return cb.conjunction();
        };
    }
}
