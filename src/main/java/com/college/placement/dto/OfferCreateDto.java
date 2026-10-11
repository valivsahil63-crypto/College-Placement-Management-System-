package com.college.placement.dto;

import com.college.placement.enums.OfferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class OfferCreateDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Job Designation is required")
    private String designation;

    @NotNull(message = "Offered CTC is required")
    @DecimalMin(value = "0.0", message = "CTC cannot be negative")
    private Double packageOffered;

    @NotNull(message = "Offer issue date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate offerDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;

    private OfferStatus status = OfferStatus.PENDING;

    private String offerDetails;

    public OfferCreateDto() {
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getPackageOffered() {
        return packageOffered;
    }

    public void setPackageOffered(Double packageOffered) {
        this.packageOffered = packageOffered;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }
}
