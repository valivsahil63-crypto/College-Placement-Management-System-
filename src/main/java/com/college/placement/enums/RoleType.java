package com.college.placement.enums;

public enum RoleType {
    ROLE_ADMIN("Administrator"),
    ROLE_OFFICER("Placement Officer"),
    ROLE_STUDENT("Student");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
