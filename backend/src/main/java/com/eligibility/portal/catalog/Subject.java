package com.eligibility.portal.catalog;

import java.util.Arrays;
import java.util.Optional;

/**
 * The 13 subjects a student may report marks for. This is the master list
 * exposed via GET /subjects and referenced by every course's required-subject set.
 */
public enum Subject {
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    MATHEMATICS("Mathematics"),
    BIOLOGY("Biology"),
    ACCOUNTANCY("Accountancy"),
    BUSINESS_STUDIES("Business Studies"),
    ECONOMICS("Economics"),
    HISTORY("History"),
    POLITICAL_SCIENCE("Political Science"),
    GEOGRAPHY("Geography"),
    PSYCHOLOGY("Psychology"),
    SOCIOLOGY("Sociology"),
    ENGLISH("English");

    private final String displayName;

    Subject(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<Subject> fromDisplayName(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(s -> s.displayName.equalsIgnoreCase(value.trim()))
                .findFirst();
    }
}
