package com.eligibility.portal.catalog;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.eligibility.portal.catalog.Subject.ACCOUNTANCY;
import static com.eligibility.portal.catalog.Subject.BIOLOGY;
import static com.eligibility.portal.catalog.Subject.BUSINESS_STUDIES;
import static com.eligibility.portal.catalog.Subject.CHEMISTRY;
import static com.eligibility.portal.catalog.Subject.ECONOMICS;
import static com.eligibility.portal.catalog.Subject.ENGLISH;
import static com.eligibility.portal.catalog.Subject.GEOGRAPHY;
import static com.eligibility.portal.catalog.Subject.HISTORY;
import static com.eligibility.portal.catalog.Subject.MATHEMATICS;
import static com.eligibility.portal.catalog.Subject.PHYSICS;
import static com.eligibility.portal.catalog.Subject.POLITICAL_SCIENCE;
import static com.eligibility.portal.catalog.Subject.PSYCHOLOGY;
import static com.eligibility.portal.catalog.Subject.SOCIOLOGY;

/**
 * The full course catalog. Declaration order is deliberate: it is the "catalog order"
 * used as the tie-break for recommendation ordering (same-stream matches first, then
 * catalog order), and mirrors the order the assessment brief itself lists courses in.
 *
 * <p>Every eligibility rule in the system is data on this enum, evaluated by the single
 * algorithm in {@link com.eligibility.portal.eligibility.EligibilityEvaluator} - adding or
 * changing a course/cutoff/required-subject set never requires touching the evaluator.
 */
public enum Course {

    COMPUTER_SCIENCE_ENGINEERING("Computer Science Engineering", Stream.ENGINEERING,
            EnumSet.of(PHYSICS, CHEMISTRY, MATHEMATICS), 75.0, true, false),
    MECHANICAL_ENGINEERING("Mechanical Engineering", Stream.ENGINEERING,
            EnumSet.of(PHYSICS, CHEMISTRY, MATHEMATICS), 70.0, true, false),
    ELECTRICAL_ENGINEERING("Electrical Engineering", Stream.ENGINEERING,
            EnumSet.of(PHYSICS, CHEMISTRY, MATHEMATICS), 70.0, true, false),
    CIVIL_ENGINEERING("Civil Engineering", Stream.ENGINEERING,
            EnumSet.of(PHYSICS, CHEMISTRY, MATHEMATICS), 65.0, true, false),
    ELECTRONICS_AND_COMMUNICATION_ENGINEERING("Electronics and Communication Engineering", Stream.ENGINEERING,
            EnumSet.of(PHYSICS, CHEMISTRY, MATHEMATICS), 70.0, true, false),

    MBBS("MBBS", Stream.MEDICINE,
            EnumSet.of(PHYSICS, CHEMISTRY, BIOLOGY), 85.0, false, true),
    BDS("BDS", Stream.MEDICINE,
            EnumSet.of(PHYSICS, CHEMISTRY, BIOLOGY), 80.0, false, true),
    BAMS("BAMS", Stream.MEDICINE,
            EnumSet.of(PHYSICS, CHEMISTRY, BIOLOGY), 75.0, false, true),
    BHMS("BHMS", Stream.MEDICINE,
            EnumSet.of(PHYSICS, CHEMISTRY, BIOLOGY), 75.0, false, true),
    BPT("BPT", Stream.MEDICINE,
            EnumSet.of(PHYSICS, CHEMISTRY, BIOLOGY), 70.0, false, true),

    B_COM("B.Com", Stream.COMMERCE,
            EnumSet.of(ACCOUNTANCY, BUSINESS_STUDIES, ECONOMICS), null, false, false),
    BBA("BBA", Stream.COMMERCE,
            EnumSet.of(ACCOUNTANCY, BUSINESS_STUDIES, ECONOMICS), null, false, false),
    BBM("BBM", Stream.COMMERCE,
            EnumSet.of(ACCOUNTANCY, BUSINESS_STUDIES, ECONOMICS), null, false, false),
    CA("CA", Stream.COMMERCE,
            EnumSet.of(ACCOUNTANCY, BUSINESS_STUDIES, ECONOMICS), null, false, false),

    BA_HISTORY("BA in History", Stream.HUMANITIES,
            EnumSet.of(HISTORY, POLITICAL_SCIENCE, GEOGRAPHY), null, false, false),
    BA_PSYCHOLOGY("BA in Psychology", Stream.HUMANITIES,
            EnumSet.of(PSYCHOLOGY, SOCIOLOGY, ENGLISH), null, false, false),
    BA_SOCIOLOGY("BA in Sociology", Stream.HUMANITIES,
            EnumSet.of(SOCIOLOGY, POLITICAL_SCIENCE, HISTORY), null, false, false),
    BA_POLITICAL_SCIENCE("BA in Political Science", Stream.HUMANITIES,
            EnumSet.of(POLITICAL_SCIENCE, HISTORY, GEOGRAPHY), null, false, false),
    BA_ENGLISH("BA in English", Stream.HUMANITIES,
            EnumSet.of(ENGLISH, HISTORY, POLITICAL_SCIENCE), null, false, false);

    private final String displayName;
    private final Stream stream;
    private final Set<Subject> requiredSubjects;
    private final Double cutoffPercentage;
    private final boolean requiresJee;
    private final boolean requiresNeet;

    Course(String displayName, Stream stream, Set<Subject> requiredSubjects, Double cutoffPercentage,
           boolean requiresJee, boolean requiresNeet) {
        this.displayName = displayName;
        this.stream = stream;
        this.requiredSubjects = Collections.unmodifiableSet(requiredSubjects);
        this.cutoffPercentage = cutoffPercentage;
        this.requiresJee = requiresJee;
        this.requiresNeet = requiresNeet;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Stream getStream() {
        return stream;
    }

    public Set<Subject> getRequiredSubjects() {
        return requiredSubjects;
    }

    /** Null means the assessment brief specifies no numeric aggregate cutoff for this course. */
    public Double getCutoffPercentage() {
        return cutoffPercentage;
    }

    public boolean isRequiresJee() {
        return requiresJee;
    }

    public boolean isRequiresNeet() {
        return requiresNeet;
    }

    public static Optional<Course> fromDisplayName(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(c -> c.displayName.equalsIgnoreCase(value.trim()))
                .findFirst();
    }
}
