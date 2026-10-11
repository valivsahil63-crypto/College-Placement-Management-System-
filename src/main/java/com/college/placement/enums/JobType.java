package com.college.placement.enums;

public enum JobType {
    FULL_TIME("Full Time"),
    INTERNSHIP("Internship"),
    INTERN_PLUS_FULLTIME("Internship + Full Time PPO");

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
