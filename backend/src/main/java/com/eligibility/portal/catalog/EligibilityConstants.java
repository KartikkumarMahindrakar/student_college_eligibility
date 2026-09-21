package com.eligibility.portal.catalog;

/**
 * Values assumed by the system where the assessment brief itself gives no explicit number.
 * See README.md "Documented assumptions" for the full rationale.
 */
public final class EligibilityConstants {

    /**
     * The assessment brief gives no numeric cutoff for Commerce/Humanities courses, and gives
     * aggregate-average cutoffs (not per-subject floors) for Engineering/Medicine. We apply a
     * uniform minimum passing mark of 40 per required subject everywhere - the standard
     * Indian secondary-education passing criterion - as the eligibility bar for Commerce/
     * Humanities, and as an additional floor under Engineering/Medicine's own cutoffs.
     */
    public static final int MINIMUM_PASS_MARK = 40;

    /** Number of subject-mark entries every student submits. */
    public static final int REQUIRED_SUBJECT_COUNT = 6;

    /** Cap on how many alternative courses are suggested when a student is rejected. */
    public static final int MAX_RECOMMENDATIONS = 3;

    private EligibilityConstants() {
    }
}
