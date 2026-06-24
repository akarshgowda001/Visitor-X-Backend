package com.visitor_x.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;


public enum PurposeOfVisit {

    MEETING("Meeting"),
    INTERVIEW("Interview"),
    INTERNSHIP("Internship"),
    FULL_TIME_EMPLOYMENT("Full Time Employment");

    private final String label;

    PurposeOfVisit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static PurposeOfVisit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose of visit is required");
        }

        String raw = value.trim();
        String normalized = raw.replace("-", "_").replace(" ", "_").toUpperCase();

        for (PurposeOfVisit purpose : values()) {
            if (purpose.name().equals(normalized) || purpose.label.equalsIgnoreCase(raw)) {
                return purpose;
            }
        }

        throw new IllegalArgumentException("Invalid purposeOfVisit: " + value);
    }
}