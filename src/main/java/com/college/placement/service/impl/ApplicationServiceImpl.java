package com.college.placement.service.impl;

import com.college.placement.dto.EligibilityResultDto;
import com.college.placement.entity.*;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.DuplicateApplicationException;
import com.college.placement.exception.IneligibleApplicationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.*;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.EligibilityService;
import com.college.placement.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final UserRepository userRepository;
    private final EligibilityService eligibilityService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  ApplicationStatusHistoryRepository statusHistoryRepository,
                                  StudentProfileRepository studentProfileRepository,
                                  PlacementDriveRepository placementDriveRepository,
                                  UserRepository userRepository,
                                  EligibilityService eligibilityService,
                                  NotificationService notificationService,
                                  AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.userRepository = userRepository;
        this.eligibilityService = eligibilityService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Application applyForDrive(Long driveId, String studentUsername, String studentRemarks) {
        // 1. Fetch student profile
        StudentProfile student = studentProfileRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + studentUsername));

        // 2. Fetch placement drive
        PlacementDrive drive = placementDriveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + driveId));

        // 3. Prevent duplicate applications
        if (applicationRepository.existsByStudentAndDrive(student, drive)) {
            throw new DuplicateApplicationException("You have already submitted an application for " + drive.getTitle() + ".");
        }

        // 4. Require uploaded resume
        if (student.getResumePath() == null || student.getResumePath().trim().isEmpty()) {
            throw new BusinessValidationException("You must upload your resume PDF in your profile before applying for placement drives.");
        }

        // 5. Server-side eligibility enforcement
        EligibilityResultDto eligibility = eligibilityService.evaluateEligibility(student, drive);
        if (!eligibility.isEligible()) {
            throw new IneligibleApplicationException(
                    "Application rejected: You do not satisfy the eligibility criteria for this drive.",
                    eligibility.getFailedCriteria()
            );
        }

        // 6. Save application
        Application app = new Application(student, drive);
        app.setStudentRemarks(studentRemarks);
        app.setCurrentStatus(ApplicationStatus.APPLIED);
        Application savedApp = applicationRepository.save(app);

        // 7. Record status history
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                savedApp, null, ApplicationStatus.APPLIED, student.getUser(), "Initial application submitted by student"
        );
        statusHistoryRepository.save(history);

        // 8. Send notification
        notificationService.sendNotification(
                student.getUser(),
                "Application Submitted Successfully",
                "Your application for '" + drive.getTitle() + "' at " + drive.getCompany().getName() + " has been received.",
                "/student/applications"
        );

        // 9. Audit log
        auditLogService.log(studentUsername, "ROLE_STUDENT", "APPLY_DRIVE",
                "Application", savedApp.getId().toString(), "Applied for drive: " + drive.getTitle(), "N/A");

        return savedApp;
    }

    @Override
    @Transactional
    public Application updateApplicationStatus(Long applicationId, ApplicationStatus newStatus,
                                              String remarks, String officerUsername) {
        Application app = getApplicationById(applicationId);
        ApplicationStatus oldStatus = app.getCurrentStatus();

        if (oldStatus == newStatus) {
            return app;
        }

        User changedBy = userRepository.findByUsername(officerUsername).orElse(null);

        app.setCurrentStatus(newStatus);
        app.setRemarks(remarks);
        Application updatedApp = applicationRepository.save(app);

        // Record history
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                updatedApp, oldStatus, newStatus, changedBy, remarks
        );
        statusHistoryRepository.save(history);

        // If selected, mark student as placed
        StudentProfile student = app.getStudent();
        if (newStatus == ApplicationStatus.SELECTED) {
            student.setPlaced(true);
            studentProfileRepository.save(student);
        } else if (oldStatus == ApplicationStatus.SELECTED) {
            // Check if any other selected application exists
            boolean hasOtherSelection = applicationRepository.findByStudentOrderByAppliedAtDesc(student)
                    .stream()
                    .anyMatch(a -> !a.getId().equals(app.getId()) && a.getCurrentStatus() == ApplicationStatus.SELECTED);
            if (!hasOtherSelection) {
                student.setPlaced(false);
                studentProfileRepository.save(student);
            }
        }

        // Notify student of stage progression
        notificationService.sendNotification(
                student.getUser(),
                "Application Status Update: " + app.getDrive().getTitle(),
                "Your application status for " + app.getDrive().getTitle() + " at " +
                        app.getDrive().getCompany().getName() + " has been updated to: " + newStatus.getDisplayName() +
                        (remarks != null && !remarks.isBlank() ? " (" + remarks + ")" : ""),
                "/student/applications"
        );

        // Audit log
        auditLogService.log(officerUsername, "OFFICER/ADMIN", "UPDATE_APPLICATION_STATUS",
                "Application", applicationId.toString(), "Status changed from " + oldStatus + " to " + newStatus, "N/A");

        return updatedApp;
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, String studentUsername, String reason) {
        Application app = getApplicationById(applicationId);

        // Security verification: ensure logged-in user is the application owner
        if (!app.getStudent().getUser().getUsername().equals(studentUsername)) {
            throw new BusinessValidationException("You are not authorized to withdraw this application.");
        }

        // Rule: Can only withdraw if in APPLIED or UNDER_REVIEW stage
        if (app.getCurrentStatus() != ApplicationStatus.APPLIED &&
            app.getCurrentStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new BusinessValidationException(
                    "Applications cannot be withdrawn after shortlisting or interview scheduling. Current stage: " +
                            app.getCurrentStatus().getDisplayName()
            );
        }

        ApplicationStatus previousStatus = app.getCurrentStatus();
        app.setCurrentStatus(ApplicationStatus.WITHDRAWN);
        app.setStudentRemarks((app.getStudentRemarks() != null ? app.getStudentRemarks() + " | " : "") +
                "Withdrawn reason: " + (reason != null ? reason : "Student requested withdrawal"));
        applicationRepository.save(app);

        // Save history
        ApplicationStatusHistory history = new ApplicationStatusHistory(
                app, previousStatus, ApplicationStatus.WITHDRAWN, app.getStudent().getUser(), reason
        );
        statusHistoryRepository.save(history);

        notificationService.sendNotification(
                app.getStudent().getUser(),
                "Application Withdrawn",
                "You have successfully withdrawn your application for " + app.getDrive().getTitle(),
                "/student/applications"
        );

        auditLogService.log(studentUsername, "ROLE_STUDENT", "WITHDRAW_APPLICATION",
                "Application", applicationId.toString(), "Withdrew application for " + app.getDrive().getTitle(), "N/A");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getStudentApplications(String studentUsername) {
        return applicationRepository.findByStudentUsername(studentUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getDriveApplications(Long driveId) {
        PlacementDrive drive = placementDriveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + driveId));
        return applicationRepository.findByDriveOrderByAppliedAtDesc(drive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationStatusHistory> getStatusHistory(Long applicationId) {
        return statusHistoryRepository.findByApplicationIdOrderByChangedAtAsc(applicationId);
    }
}
