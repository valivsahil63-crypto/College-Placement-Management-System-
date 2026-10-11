package com.college.placement.dto;

import com.college.placement.enums.DriveStatus;
import com.college.placement.enums.JobType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class PlacementDriveDto {

    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;

    @NotNull(message = "Company must be selected")
    private Long companyId;

    @NotBlank(message = "Job location is required")
    private String jobLocation;

    @NotNull(message = "Job type is required")
    private JobType jobType = JobType.FULL_TIME;

    @NotBlank(message = "At least one eligible branch must be specified")
    private String requiredBranches; // e.g. "CSE,ISE,ECE"

    @NotNull(message = "Minimum CGPA threshold is required")
    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private Double minCgpa;

    @NotNull(message = "Minimum 10th percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100.0")
    private Double minTenthPercentage;

    @NotNull(message = "Minimum 12th percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100.0")
    private Double minTwelfthPercentage;

    @NotNull(message = "Maximum backlogs limit is required")
    @Min(value = 0, message = "Backlogs cannot be negative")
    private Integer maxActiveBacklogs = 0;

    @NotNull(message = "Eligible graduation year is required")
    private Integer eligibleGraduationYear;

    private String requiredSkills;

    @NotNull(message = "CTC salary package is required")
    @DecimalMin(value = "0.0", message = "CTC cannot be negative")
    private Double ctcSalary;

    @NotNull(message = "Application opening date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime applicationOpeningDate;

    @NotNull(message = "Application closing date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime applicationClosingDate;

    @NotNull(message = "Vacancies count is required")
    @Min(value = 1, message = "Vacancies must be at least 1")
    private Integer vacancies = 1;

    private String selectionProcess;

    private DriveStatus status = DriveStatus.PUBLISHED;

    public PlacementDriveDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getRequiredBranches() {
        return requiredBranches;
    }

    public void setRequiredBranches(String requiredBranches) {
        this.requiredBranches = requiredBranches;
    }

    public Double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public Double getMinTenthPercentage() {
        return minTenthPercentage;
    }

    public void setMinTenthPercentage(Double minTenthPercentage) {
        this.minTenthPercentage = minTenthPercentage;
    }

    public Double getMinTwelfthPercentage() {
        return minTwelfthPercentage;
    }

    public void setMinTwelfthPercentage(Double minTwelfthPercentage) {
        this.minTwelfthPercentage = minTwelfthPercentage;
    }

    public Integer getMaxActiveBacklogs() {
        return maxActiveBacklogs;
    }

    public void setMaxActiveBacklogs(Integer maxActiveBacklogs) {
        this.maxActiveBacklogs = maxActiveBacklogs;
    }

    public Integer getEligibleGraduationYear() {
        return eligibleGraduationYear;
    }

    public void setEligibleGraduationYear(Integer eligibleGraduationYear) {
        this.eligibleGraduationYear = eligibleGraduationYear;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Double getCtcSalary() {
        return ctcSalary;
    }

    public void setCtcSalary(Double ctcSalary) {
        this.ctcSalary = ctcSalary;
    }

    public LocalDateTime getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public void setApplicationOpeningDate(LocalDateTime applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public LocalDateTime getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public void setApplicationClosingDate(LocalDateTime applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    public Integer getVacancies() {
        return vacancies;
    }

    public void setVacancies(Integer vacancies) {
        this.vacancies = vacancies;
    }

    public String getSelectionProcess() {
        return selectionProcess;
    }

    public void setSelectionProcess(String selectionProcess) {
        this.selectionProcess = selectionProcess;
    }

    public DriveStatus getStatus() {
        return status;
    }

    public void setStatus(DriveStatus status) {
        this.status = status;
    }
}
