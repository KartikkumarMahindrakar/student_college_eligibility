package com.eligibility.portal.eligibility;

import com.eligibility.portal.catalog.Subject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Wraps a student's submitted subject-to-marks map with the lookups the evaluator needs. */
public final class SubjectMarks {

    private final Map<Subject, Integer> marksBySubject;

    public SubjectMarks(Map<Subject, Integer> marksBySubject) {
        this.marksBySubject = marksBySubject;
    }

    public Set<Subject> subjectsPresent() {
        return marksBySubject.keySet();
    }

    public boolean hasSubject(Subject subject) {
        return marksBySubject.containsKey(subject);
    }

    public int marksFor(Subject subject) {
        Integer marks = marksBySubject.get(subject);
        if (marks == null) {
            throw new IllegalArgumentException("No marks submitted for " + subject);
        }
        return marks;
    }

    /** Subjects from the given set that the student did not submit at all. */
    public Set<Subject> missingFrom(Set<Subject> required) {
        return required.stream()
                .filter(s -> !hasSubject(s))
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    /** Of the required subjects the student DID submit, those below the given floor. */
    public Set<Subject> belowFloor(Set<Subject> required, int floor) {
        return required.stream()
                .filter(this::hasSubject)
                .filter(s -> marksFor(s) < floor)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    /** Average of the given subjects' marks, rounded HALF_UP to 2 decimals. */
    public BigDecimal averageOf(Set<Subject> subjects) {
        double sum = subjects.stream().mapToInt(this::marksFor).sum();
        double average = sum / subjects.size();
        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
    }
}
