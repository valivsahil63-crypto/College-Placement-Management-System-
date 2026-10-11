package com.college.placement.enums;

public enum ApplicationStatus {
    APPLIED("Applied", "primary"),
    UNDER_REVIEW("Under Review", "info"),
    SHORTLISTED("Shortlisted", "warning"),
    INTERVIEW_SCHEDULED("Interview Scheduled", "primary"),
    SELECTED("Selected", "success"),
    REJECTED("Rejected", "danger"),
    WITHDRAWN("Withdrawn", "secondary");

    private final String displayName;
    private final String badgeClass;

    ApplicationStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
