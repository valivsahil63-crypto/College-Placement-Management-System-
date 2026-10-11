package com.college.placement.dto;

import java.util.ArrayList;
import java.util.List;

public class EligibilityResultDto {

    public enum StatusType {
        ELIGIBLE("Eligible to Apply", "success"),
        NOT_ELIGIBLE("Not Eligible", "danger"),
        APPLICATION_CLOSED("Application Closed", "warning"),
        NOT_YET_OPEN("Registration Not Yet Open", "info"),
        ALREADY_APPLIED("Already Applied", "primary");

        private final String label;
        private final String badgeColor;

        StatusType(String label, String badgeColor) {
            this.label = label;
            this.badgeColor = badgeColor;
        }

        public String getLabel() {
            return label;
        }

        public String getBadgeColor() {
            return badgeColor;
        }
    }

    private boolean eligible;
    private StatusType statusType;
    private List<String> failedCriteria = new ArrayList<>();
    private List<String> satisfiedCriteria = new ArrayList<>();
    private boolean alreadyApplied;
    private boolean resumeUploaded;

    // Boundary comparison diagnostics for UI explanation & viva
    private Double studentCgpa;
    private Double minCgpa;
    private Double studentTenth;
    private Double minTenth;
    private Double studentTwelfth;
    private Double minTwelfth;
    private Integer studentBacklogs;
    private Integer maxBacklogs;
    private String studentBranch;
    private String allowedBranches;
    private Integer studentGradYear;
    private Integer eligibleGradYear;

    public EligibilityResultDto() {
    }

    public void addFailure(String reason) {
        this.failedCriteria.add(reason);
    }

    public void addSatisfied(String rule) {
        this.satisfiedCriteria.add(rule);
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public List<String> getFailedCriteria() {
        return failedCriteria;
    }

    public void setFailedCriteria(List<String> failedCriteria) {
        this.failedCriteria = failedCriteria;
    }

    public List<String> getSatisfiedCriteria() {
        return satisfiedCriteria;
    }

    public void setSatisfiedCriteria(List<String> satisfiedCriteria) {
        this.satisfiedCriteria = satisfiedCriteria;
    }

    public boolean isAlreadyApplied() {
        return alreadyApplied;
    }

    public void setAlreadyApplied(boolean alreadyApplied) {
        this.alreadyApplied = alreadyApplied;
    }

    public boolean isResumeUploaded() {
        return resumeUploaded;
    }

    public void setResumeUploaded(boolean resumeUploaded) {
        this.resumeUploaded = resumeUploaded;
    }

    public Double getStudentCgpa() {
        return studentCgpa;
    }

    public void setStudentCgpa(Double studentCgpa) {
        this.studentCgpa = studentCgpa;
    }

    public Double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public Double getStudentTenth() {
        return studentTenth;
    }

    public void setStudentTenth(Double studentTenth) {
        this.studentTenth = studentTenth;
    }

    public Double getMinTenth() {
        return minTenth;
    }

    public void setMinTenth(Double minTenth) {
        this.minTenth = minTenth;
    }

    public Double getStudentTwelfth() {
        return studentTwelfth;
    }

    public void setStudentTwelfth(Double studentTwelfth) {
        this.studentTwelfth = studentTwelfth;
    }

    public Double getMinTwelfth() {
        return minTwelfth;
    }

    public void setMinTwelfth(Double minTwelfth) {
        this.minTwelfth = minTwelfth;
    }

    public Integer getStudentBacklogs() {
        return studentBacklogs;
    }

    public void setStudentBacklogs(Integer studentBacklogs) {
        this.studentBacklogs = studentBacklogs;
    }

    public Integer getMaxBacklogs() {
        return maxBacklogs;
    }

    public void setMaxBacklogs(Integer maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(String studentBranch) {
        this.studentBranch = studentBranch;
    }

    public String getAllowedBranches() {
        return allowedBranches;
    }

    public void setAllowedBranches(String allowedBranches) {
        this.allowedBranches = allowedBranches;
    }

    public Integer getStudentGradYear() {
        return studentGradYear;
    }

    public void setStudentGradYear(Integer studentGradYear) {
        this.studentGradYear = studentGradYear;
    }

    public Integer getEligibleGradYear() {
        return eligibleGradYear;
    }

    public void setEligibleGradYear(Integer eligibleGradYear) {
        this.eligibleGradYear = eligibleGradYear;
    }
}
