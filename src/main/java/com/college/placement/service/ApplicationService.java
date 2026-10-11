package com.college.placement.service;

import com.college.placement.entity.Application;
import com.college.placement.entity.ApplicationStatusHistory;
import com.college.placement.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {
    Application applyForDrive(Long driveId, String studentUsername, String studentRemarks);
    Application updateApplicationStatus(Long applicationId, ApplicationStatus newStatus, String remarks, String officerUsername);
    void withdrawApplication(Long applicationId, String studentUsername, String reason);
    List<Application> getStudentApplications(String studentUsername);
    List<Application> getDriveApplications(Long driveId);
    List<Application> getAllApplications();
    Application getApplicationById(Long id);
    List<ApplicationStatusHistory> getStatusHistory(Long applicationId);
}
