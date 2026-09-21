package com.eligibility.portal.eligibility;

import com.eligibility.portal.catalog.Course;
import com.eligibility.portal.catalog.Subject;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationServiceTest {

    private final RecommendationService recommendationService = new RecommendationService(new EligibilityEvaluator());

    private SubjectMarks marksOf(Object... subjectMarkPairs) {
        Map<Subject, Integer> map = new EnumMap<>(Subject.class);
        for (int i = 0; i < subjectMarkPairs.length; i += 2) {
            map.put((Subject) subjectMarkPairs[i], (Integer) subjectMarkPairs[i + 1]);
        }
        return new SubjectMarks(map);
    }

    @Test
    void returnsEmptyListNotNullWhenNoAlternativesAreEligible() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 10);
        List<Course> result = recommendationService.recommendAlternatives(
                Course.COMPUTER_SCIENCE_ENGINEERING, marks, false, false);
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void returnsExactlyOneWhenOnlyOneCourseIsSatisfied() {
        // Physics+Chemistry+Sociology+English uniquely satisfies BA in Psychology only.
        SubjectMarks marks = marksOf(Subject.PSYCHOLOGY, 60, Subject.SOCIOLOGY, 60, Subject.ENGLISH, 60);
        List<Course> result = recommendationService.recommendAlternatives(Course.MBBS, marks, false, false);
        assertThat(result).containsExactly(Course.BA_PSYCHOLOGY);
    }

    @Test
    void capsAtThreeWhenMoreAlternativesAreEligible() {
        // All four Commerce courses share identical requirements - satisfying one satisfies all four.
        SubjectMarks marks = marksOf(Subject.ACCOUNTANCY, 60, Subject.BUSINESS_STUDIES, 60, Subject.ECONOMICS, 60);
        List<Course> result = recommendationService.recommendAlternatives(Course.MBBS, marks, false, false);
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(Course.B_COM, Course.BBA, Course.BBM);
    }

    @Test
    void sameStreamAlternativesSortBeforeOtherStreamAlternatives() {
        // PCM average 68 with JEE qualified clears only Civil (65) within Engineering, while
        // Commerce subjects at 60 make all four Commerce courses eligible too. Civil (same
        // stream as the rejected CSE) must lead, then Commerce fills the remaining slots in
        // catalog order.
        SubjectMarks marks = marksOf(
                Subject.PHYSICS, 68, Subject.CHEMISTRY, 68, Subject.MATHEMATICS, 68,
                Subject.ACCOUNTANCY, 60, Subject.BUSINESS_STUDIES, 60, Subject.ECONOMICS, 60);
        List<Course> result = recommendationService.recommendAlternatives(
                Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result).containsExactly(Course.CIVIL_ENGINEERING, Course.B_COM, Course.BBA);
    }

    @Test
    void neverIncludesTheRejectedCourseItself() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 90, Subject.CHEMISTRY, 90, Subject.MATHEMATICS, 90);
        List<Course> result = recommendationService.recommendAlternatives(
                Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result).doesNotContain(Course.COMPUTER_SCIENCE_ENGINEERING);
    }

    @Test
    void worksRegardlessOfWhyTheOriginalCourseWasRejected() {
        // Rejected from MBBS for an exam-qualification reason, not a subject/marks reason -
        // recommendations must still surface based purely on marks/exam status, not the reason.
        SubjectMarks marks = marksOf(Subject.ACCOUNTANCY, 60, Subject.BUSINESS_STUDIES, 60, Subject.ECONOMICS, 60);
        List<Course> result = recommendationService.recommendAlternatives(Course.MBBS, marks, false, false);
        assertThat(result).contains(Course.B_COM, Course.BBA, Course.BBM);
    }
}
