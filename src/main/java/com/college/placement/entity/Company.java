package com.college.placement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_company_name", columnList = "name")
})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Industry sector is required")
    @Column(nullable = false, length = 100)
    private String industry;

    @Column(length = 200)
    private String website;

    @NotBlank(message = "HR contact person name is required")
    @Column(name = "hr_contact_name", nullable = false, length = 100)
    private String hrContactName;

    @NotBlank(message = "HR contact email is required")
    @Email(message = "Valid HR email is required")
    @Column(name = "hr_contact_email", nullable = false, length = 120)
    private String hrContactEmail;

    @Column(name = "hr_contact_phone", length = 20)
    private String hrContactPhone;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @NotBlank(message = "Location is required")
    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Company() {
    }

    public Company(String name, String industry, String location, String hrContactName, String hrContactEmail) {
        this.name = name;
        this.industry = industry;
        this.location = location;
        this.hrContactName = hrContactName;
        this.hrContactEmail = hrContactEmail;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getHrContactName() {
        return hrContactName;
    }

    public void setHrContactName(String hrContactName) {
        this.hrContactName = hrContactName;
    }

    public String getHrContactEmail() {
        return hrContactEmail;
    }

    public void setHrContactEmail(String hrContactEmail) {
        this.hrContactEmail = hrContactEmail;
    }

    public String getHrContactPhone() {
        return hrContactPhone;
    }

    public void setHrContactPhone(String hrContactPhone) {
        this.hrContactPhone = hrContactPhone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
