package com.eligibility.portal.eligibility;

/** Outcome of evaluating one student against one course. */
public final class EligibilityCheckResult {

    private final boolean eligible;
    private final String reason;

    private EligibilityCheckResult(boolean eligible, String reason) {
        this.eligible = eligible;
        this.reason = reason;
    }

    public static EligibilityCheckResult eligible(String reason) {
        return new EligibilityCheckResult(true, reason);
    }

    public static EligibilityCheckResult rejected(String reason) {
        return new EligibilityCheckResult(false, reason);
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getReason() {
        return reason;
    }
}
