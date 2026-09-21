package com.eligibility.portal.eligibility;

import com.eligibility.portal.catalog.Course;
import com.eligibility.portal.catalog.EligibilityConstants;
import com.eligibility.portal.catalog.Subject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The single algorithm behind every eligibility decision in the system. Every course's rule
 * (required exam, required subjects, cutoff) is data on {@link Course} - this class never
 * branches per stream, only per rule category, in a fixed order chosen so the rejection
 * reason returned is always the earliest, most specific problem rather than a generic
 * "not eligible":
 *
 * <ol>
 *   <li>exam qualification (JEE/NEET), if the course requires one</li>
 *   <li>presence of every required subject among the student's submitted marks</li>
 *   <li>each required subject clearing the minimum passing mark</li>
 *   <li>if the course defines an aggregate cutoff, the average of its required subjects meeting it</li>
 * </ol>
 */
@Component
public class EligibilityEvaluator {

    public EligibilityCheckResult evaluate(Course course, SubjectMarks marks, boolean jeeQualified, boolean neetQualified) {
        if (course.isRequiresJee() && !jeeQualified) {
            return EligibilityCheckResult.rejected(
                    "Not eligible for " + course.getDisplayName() + ": JEE qualification is required.");
        }
        if (course.isRequiresNeet() && !neetQualified) {
            return EligibilityCheckResult.rejected(
                    "Not eligible for " + course.getDisplayName() + ": NEET qualification is required.");
        }

        Set<Subject> required = course.getRequiredSubjects();

        Set<Subject> missing = marks.missingFrom(required);
        if (!missing.isEmpty()) {
            return EligibilityCheckResult.rejected(
                    "Not eligible for " + course.getDisplayName() + ": missing required subject(s) "
                            + quoteJoin(missing) + ".");
        }

        Set<Subject> belowFloor = marks.belowFloor(required, EligibilityConstants.MINIMUM_PASS_MARK);
        if (!belowFloor.isEmpty()) {
            String detail = belowFloor.stream()
                    .map(s -> s.getDisplayName() + " (" + marks.marksFor(s) + ")")
                    .collect(Collectors.joining(", "));
            return EligibilityCheckResult.rejected(
                    "Not eligible for " + course.getDisplayName() + ": " + detail
                            + " marks are below the minimum passing mark of "
                            + EligibilityConstants.MINIMUM_PASS_MARK + ".");
        }

        Double cutoff = course.getCutoffPercentage();
        if (cutoff != null) {
            BigDecimal average = marks.averageOf(required);
            if (average.doubleValue() < cutoff) {
                return EligibilityCheckResult.rejected(
                        "Not eligible for " + course.getDisplayName() + ": average of " + subjectNames(required)
                                + " is " + average + "%, required minimum is " + formatPercentage(cutoff) + "%.");
            }
        }

        return EligibilityCheckResult.eligible("Meets all eligibility criteria");
    }

    private String quoteJoin(Set<Subject> subjects) {
        return subjects.stream()
                .map(s -> "'" + s.getDisplayName() + "'")
                .collect(Collectors.joining(", "));
    }

    private String subjectNames(Set<Subject> subjects) {
        return subjects.stream().map(Subject::getDisplayName).collect(Collectors.joining(", "));
    }

    private String formatPercentage(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
