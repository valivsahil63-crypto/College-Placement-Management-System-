package com.college.placement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "student_profiles", indexes = {
    @Index(name = "idx_student_usn", columnList = "usn"),
    @Index(name = "idx_student_branch", columnList = "branch"),
    @Index(name = "idx_student_grad_year", columnList = "graduation_year")
})
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "USN / Roll Number is required")
    @Column(nullable = false, unique = true, length = 30)
    private String usn;

    @NotBlank(message = "Contact phone number is required")
    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 15)
    private String gender;

    @NotBlank(message = "Department / Branch is required")
    @Column(nullable = false, length = 50)
    private String branch;

    @NotNull(message = "Graduation Year is required")
    @Min(value = 2000, message = "Graduation year must be valid")
    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", inclusive = true, message = "CGPA cannot exceed 10.0")
    @Column(nullable = false)
    private Double cgpa;

    @NotNull(message = "10th Grade percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Percentage cannot exceed 100.0")
    @Column(name = "tenth_percentage", nullable = false)
    private Double tenthPercentage;

    @NotNull(message = "12th Grade percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Percentage cannot exceed 100.0")
    @Column(name = "twelfth_percentage", nullable = false)
    private Double twelfthPercentage;

    @NotNull(message = "Active backlogs count is required")
    @Min(value = 0, message = "Active backlogs cannot be negative")
    @Column(name = "active_backlogs", nullable = false)
    private Integer activeBacklogs = 0;

    @Column(name = "resume_path", length = 255)
    private String resumePath;

    @Column(name = "resume_file_name", length = 255)
    private String resumeFileName;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "is_placed", nullable = false)
    private boolean placed = false;

    public StudentProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Double getTenthPercentage() {
        return tenthPercentage;
    }

    public void setTenthPercentage(Double tenthPercentage) {
        this.tenthPercentage = tenthPercentage;
    }

    public Double getTwelfthPercentage() {
        return twelfthPercentage;
    }

    public void setTwelfthPercentage(Double twelfthPercentage) {
        this.twelfthPercentage = twelfthPercentage;
    }

    public Integer getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(Integer activeBacklogs) {
        this.activeBacklogs = activeBacklogs;
    }

    public String getResumePath() {
        return resumePath;
    }

    public void setResumePath(String resumePath) {
        this.resumePath = resumePath;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
}
