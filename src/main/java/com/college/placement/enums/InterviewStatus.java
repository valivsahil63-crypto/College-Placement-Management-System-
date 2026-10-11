package com.college.placement.enums;

public enum InterviewStatus {
    SCHEDULED("Scheduled", "primary"),
    COMPLETED("Completed", "success"),
    RESCHEDULED("Rescheduled", "warning"),
    CANCELLED("Cancelled", "danger");

    private final String displayName;
    private final String badgeClass;

    InterviewStatus(String displayName, String badgeClass) {
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
