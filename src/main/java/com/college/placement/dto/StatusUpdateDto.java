package com.college.placement.dto;

import com.college.placement.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "New application status is required")
    private ApplicationStatus newStatus;

    private String remarks;

    public StatusUpdateDto() {
    }

    public StatusUpdateDto(Long applicationId, ApplicationStatus newStatus, String remarks) {
        this.applicationId = applicationId;
        this.newStatus = newStatus;
        this.remarks = remarks;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public ApplicationStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ApplicationStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
