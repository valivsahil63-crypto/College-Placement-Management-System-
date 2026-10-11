package com.college.placement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CompanyDto {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;

    @NotBlank(message = "Industry sector is required")
    private String industry;

    private String website;

    @NotBlank(message = "HR contact name is required")
    private String hrContactName;

    @NotBlank(message = "HR contact email is required")
    @Email(message = "Valid HR email is required")
    private String hrContactEmail;

    private String hrContactPhone;
    private String logoUrl;

    @NotBlank(message = "Company location is required")
    private String location;

    private boolean active = true;

    public CompanyDto() {
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
}
