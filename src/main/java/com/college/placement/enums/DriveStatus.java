package com.college.placement.enums;

public enum DriveStatus {
    DRAFT("Draft", "secondary"),
    PUBLISHED("Open for Registration", "success"),
    REGISTRATION_CLOSED("Registration Closed", "warning"),
    COMPLETED("Completed", "info"),
    CANCELLED("Cancelled", "danger");

    private final String displayName;
    private final String badgeClass;

    DriveStatus(String displayName, String badgeClass) {
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
