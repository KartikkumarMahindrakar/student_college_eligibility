package com.eligibility.portal.eligibility;

import com.eligibility.portal.catalog.Course;
import com.eligibility.portal.catalog.EligibilityConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * When a student is rejected from their desired course, finds up to
 * {@link EligibilityConstants#MAX_RECOMMENDATIONS} alternative courses they would actually
 * be eligible for: same stream as the rejected course first, then catalog declaration order.
 */
@Component
public class RecommendationService {

    private final EligibilityEvaluator evaluator;

    public RecommendationService(EligibilityEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public List<Course> recommendAlternatives(Course rejectedCourse, SubjectMarks marks,
                                               boolean jeeQualified, boolean neetQualified) {
        List<Course> eligibleAlternatives = new ArrayList<>();
        for (Course candidate : Course.values()) {
            if (candidate == rejectedCourse) {
                continue;
            }
            EligibilityCheckResult result = evaluator.evaluate(candidate, marks, jeeQualified, neetQualified);
            if (result.isEligible()) {
                eligibleAlternatives.add(candidate);
            }
        }

        // Stable sort: Course.values() already yields catalog order, so grouping by
        // same-stream-first preserves catalog order within each group.
        eligibleAlternatives.sort(Comparator.comparingInt(c -> c.getStream() == rejectedCourse.getStream() ? 0 : 1));

        if (eligibleAlternatives.size() > EligibilityConstants.MAX_RECOMMENDATIONS) {
            return new ArrayList<>(eligibleAlternatives.subList(0, EligibilityConstants.MAX_RECOMMENDATIONS));
        }
        return eligibleAlternatives;
    }
}
