package com.college.placement.entity;

import com.college.placement.enums.DriveStatus;
import com.college.placement.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_drives", indexes = {
    @Index(name = "idx_drive_status", columnList = "status"),
    @Index(name = "idx_drive_grad_year", columnList = "eligible_graduation_year")
})
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "Job location is required")
    @Column(name = "job_location", nullable = false, length = 100)
    private String jobLocation;

    @NotNull(message = "Job type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 30)
    private JobType jobType = JobType.FULL_TIME;

    @NotBlank(message = "Eligible branches are required")
    @Column(name = "required_branches", nullable = false, length = 200)
    private String requiredBranches; // Comma separated, e.g. "CSE,ISE,ECE"

    @NotNull(message = "Minimum CGPA threshold is required")
    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10.0")
    @Column(name = "min_cgpa", nullable = false)
    private Double minCgpa;

    @NotNull(message = "Minimum 10th percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100.0")
    @Column(name = "min_tenth_percentage", nullable = false)
    private Double minTenthPercentage;

    @NotNull(message = "Minimum 12th percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100.0")
    @Column(name = "min_twelfth_percentage", nullable = false)
    private Double minTwelfthPercentage;

    @NotNull(message = "Maximum backlogs allowed is required")
    @Min(value = 0, message = "Backlogs cannot be negative")
    @Column(name = "max_active_backlogs", nullable = false)
    private Integer maxActiveBacklogs = 0;

    @NotNull(message = "Eligible graduation year is required")
    @Column(name = "eligible_graduation_year", nullable = false)
    private Integer eligibleGraduationYear;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @NotNull(message = "CTC / Salary package is required")
    @DecimalMin(value = "0.0", message = "CTC cannot be negative")
    @Column(name = "ctc_salary", nullable = false)
    private Double ctcSalary; // in LPA (Lakhs Per Annum)

    @NotNull(message = "Application opening date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "application_opening_date", nullable = false)
    private LocalDateTime applicationOpeningDate;

    @NotNull(message = "Application closing date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "application_closing_date", nullable = false)
    private LocalDateTime applicationClosingDate;

    @NotNull(message = "Vacancies count is required")
    @Min(value = 1, message = "Vacancies must be at least 1")
    @Column(nullable = false)
    private Integer vacancies = 1;

    @Column(name = "selection_process", columnDefinition = "TEXT")
    private String selectionProcess;

    @NotNull(message = "Drive status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DriveStatus status = DriveStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PlacementDrive() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
        return skillsOrEmpty();
    }

    private String skillsOrEmpty() {
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCurrentlyOpen() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == DriveStatus.PUBLISHED &&
               (applicationOpeningDate == null || !now.isBefore(applicationOpeningDate)) &&
               (applicationClosingDate == null || !now.isAfter(applicationClosingDate));
    }
}
