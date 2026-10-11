package com.college.placement.enums;

public enum InterviewMode {
    ONLINE("Online / Virtual"),
    OFFLINE("In-Person / On Campus");

    private final String displayName;

    InterviewMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
