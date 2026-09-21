package com.eligibility.portal.eligibility;

import com.eligibility.portal.catalog.Course;
import com.eligibility.portal.catalog.Subject;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EligibilityEvaluatorTest {

    private final EligibilityEvaluator evaluator = new EligibilityEvaluator();

    private SubjectMarks marksOf(Object... subjectMarkPairs) {
        Map<Subject, Integer> map = new EnumMap<>(Subject.class);
        for (int i = 0; i < subjectMarkPairs.length; i += 2) {
            map.put((Subject) subjectMarkPairs[i], (Integer) subjectMarkPairs[i + 1]);
        }
        return new SubjectMarks(map);
    }

    @Test
    void engineering_rejectsWhenJeeNotQualified() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 90, Subject.CHEMISTRY, 90, Subject.MATHEMATICS, 90);
        EligibilityCheckResult result = evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, false, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("JEE qualification is required");
    }

    @Test
    void engineering_rejectsWhenRequiredSubjectMissing() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 90, Subject.CHEMISTRY, 90); // no Mathematics
        EligibilityCheckResult result = evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("Mathematics");
    }

    @Test
    void engineering_rejectsOnBelowFloorEvenIfAverageWouldClearCutoff() {
        // Average of (100,100,39) = 79.67, above the 75 cutoff - but Mathematics itself is below
        // the 40 floor, which must reject before the aggregate average is even considered.
        SubjectMarks marks = marksOf(Subject.PHYSICS, 100, Subject.CHEMISTRY, 100, Subject.MATHEMATICS, 39);
        EligibilityCheckResult result = evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("Mathematics (39)", "minimum passing mark of 40");
    }

    @Test
    void engineering_rejectsWhenAverageOnePointUnderCutoff() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 74, Subject.CHEMISTRY, 74, Subject.MATHEMATICS, 74);
        EligibilityCheckResult result = evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("74.00", "75");
    }

    @Test
    void engineering_eligibleWhenAverageExactlyAtCutoff() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 75, Subject.CHEMISTRY, 75, Subject.MATHEMATICS, 75);
        EligibilityCheckResult result = evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result.isEligible()).isTrue();
    }

    @Test
    void engineering_eligibleWhenAverageAboveCutoff() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 90, Subject.CHEMISTRY, 90, Subject.MATHEMATICS, 90);
        EligibilityCheckResult result = evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false);
        assertThat(result.isEligible()).isTrue();
        assertThat(result.getReason()).isEqualTo("Meets all eligibility criteria");
    }

    @Test
    void engineering_differentCoursesSameStreamUseTheirOwnCutoff() {
        // Average = 68: fails CSE(75)/Mechanical(70)/Electrical(70)/ECE(70) but clears Civil(65).
        SubjectMarks marks = marksOf(Subject.PHYSICS, 68, Subject.CHEMISTRY, 68, Subject.MATHEMATICS, 68);
        assertThat(evaluator.evaluate(Course.COMPUTER_SCIENCE_ENGINEERING, marks, true, false).isEligible()).isFalse();
        assertThat(evaluator.evaluate(Course.CIVIL_ENGINEERING, marks, true, false).isEligible()).isTrue();
    }

    @Test
    void medicine_rejectsWhenNeetNotQualified() {
        SubjectMarks marks = marksOf(Subject.PHYSICS, 90, Subject.CHEMISTRY, 90, Subject.BIOLOGY, 90);
        EligibilityCheckResult result = evaluator.evaluate(Course.MBBS, marks, false, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("NEET qualification is required");
    }

    @Test
    void medicine_rejectsWhenBiologyMissingEvenIfMathematicsPresent() {
        // Engineering-shaped submission (PCM) evaluated against a Medicine course.
        SubjectMarks marks = marksOf(Subject.PHYSICS, 90, Subject.CHEMISTRY, 90, Subject.MATHEMATICS, 90);
        EligibilityCheckResult result = evaluator.evaluate(Course.MBBS, marks, false, true);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("Biology");
        assertThat(result.getReason()).doesNotContain("Mathematics");
    }

    @Test
    void commerce_eligibleWhenAllThreeSubjectsClearFloor_regardlessOfWhichCourse() {
        SubjectMarks marks = marksOf(Subject.ACCOUNTANCY, 40, Subject.BUSINESS_STUDIES, 40, Subject.ECONOMICS, 40);
        assertThat(evaluator.evaluate(Course.B_COM, marks, false, false).isEligible()).isTrue();
        assertThat(evaluator.evaluate(Course.BBA, marks, false, false).isEligible()).isTrue();
        assertThat(evaluator.evaluate(Course.BBM, marks, false, false).isEligible()).isTrue();
        assertThat(evaluator.evaluate(Course.CA, marks, false, false).isEligible()).isTrue();
    }

    @Test
    void commerce_rejectsWhenOneSubjectBelowFloor() {
        SubjectMarks marks = marksOf(Subject.ACCOUNTANCY, 39, Subject.BUSINESS_STUDIES, 90, Subject.ECONOMICS, 90);
        EligibilityCheckResult result = evaluator.evaluate(Course.B_COM, marks, false, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("Accountancy (39)");
    }

    @Test
    void commerce_rejectsWhenRequiredSubjectMissing() {
        SubjectMarks marks = marksOf(Subject.ACCOUNTANCY, 90, Subject.BUSINESS_STUDIES, 90);
        EligibilityCheckResult result = evaluator.evaluate(Course.CA, marks, false, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("Economics");
    }

    @Test
    void humanities_baHistory_eligibleWithFullTriplet() {
        SubjectMarks marks = marksOf(Subject.HISTORY, 60, Subject.POLITICAL_SCIENCE, 60, Subject.GEOGRAPHY, 60);
        assertThat(evaluator.evaluate(Course.BA_HISTORY, marks, false, false).isEligible()).isTrue();
    }

    @Test
    void humanities_overlappingTripletsAreNotInterchangeable() {
        // Satisfies BA Sociology's neighbor set (History, Political Science) but not its actual
        // requirement (Sociology, Political Science, History) - 2 of 3 subjects overlap with the
        // student's marks, which must not be enough.
        SubjectMarks marks = marksOf(Subject.HISTORY, 60, Subject.POLITICAL_SCIENCE, 60, Subject.GEOGRAPHY, 60);
        assertThat(evaluator.evaluate(Course.BA_HISTORY, marks, false, false).isEligible()).isTrue();
        EligibilityCheckResult sociologyResult = evaluator.evaluate(Course.BA_SOCIOLOGY, marks, false, false);
        assertThat(sociologyResult.isEligible()).isFalse();
        assertThat(sociologyResult.getReason()).contains("Sociology");
    }

    @Test
    void humanities_baEnglish_rejectsWhenEnglishMissing() {
        SubjectMarks marks = marksOf(Subject.HISTORY, 60, Subject.POLITICAL_SCIENCE, 60);
        EligibilityCheckResult result = evaluator.evaluate(Course.BA_ENGLISH, marks, false, false);
        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("English");
    }
}
