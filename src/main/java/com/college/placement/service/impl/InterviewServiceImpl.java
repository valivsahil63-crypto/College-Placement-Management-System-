package com.college.placement.service.impl;

import com.college.placement.dto.InterviewScheduleDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.Interview;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.enums.InterviewStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.repository.InterviewRepository;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.InterviewService;
import com.college.placement.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public InterviewServiceImpl(InterviewRepository interviewRepository,
                                ApplicationRepository applicationRepository,
                                ApplicationService applicationService,
                                NotificationService notificationService,
                                AuditLogService auditLogService) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Interview scheduleInterview(InterviewScheduleDto dto, String officerUsername) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + dto.getApplicationId()));

        if (dto.getInterviewDate().isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException("Interview date and time cannot be set in the past.");
        }

        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setRoundName(dto.getRoundName().trim());
        interview.setInterviewDate(dto.getInterviewDate());
        interview.setMode(dto.getMode());
        interview.setMeetingLinkOrVenue(dto.getMeetingLinkOrVenue().trim());
        interview.setInstructions(dto.getInstructions());
        interview.setStatus(InterviewStatus.SCHEDULED);

        Interview saved = interviewRepository.save(interview);

        // Advance application lifecycle state to INTERVIEW_SCHEDULED
        if (application.getCurrentStatus() != ApplicationStatus.INTERVIEW_SCHEDULED) {
            applicationService.updateApplicationStatus(
                    application.getId(),
                    ApplicationStatus.INTERVIEW_SCHEDULED,
                    "Interview scheduled: " + dto.getRoundName(),
                    officerUsername
            );
        }

        // Notify student
        String dateStr = dto.getInterviewDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        notificationService.sendNotification(
                application.getStudent().getUser(),
                "Interview Scheduled: " + dto.getRoundName(),
                "Your " + dto.getRoundName() + " for " + application.getDrive().getCompany().getName() +
                        " is scheduled on " + dateStr + ". Mode: " + dto.getMode().getDisplayName() +
                        ". Details: " + dto.getMeetingLinkOrVenue(),
                "/student/interviews"
        );

        auditLogService.log(officerUsername, "OFFICER/ADMIN", "SCHEDULE_INTERVIEW",
                "Interview", saved.getId().toString(), "Scheduled " + dto.getRoundName() + " for student " +
                        application.getStudent().getUser().getFullName(), "N/A");

        return saved;
    }

    @Override
    @Transactional
    public Interview updateInterview(Long id, InterviewStatus status, String feedback, String officerUsername) {
        Interview interview = getInterviewById(id);
        interview.setStatus(status);
        interview.setFeedback(feedback);

        Interview updated = interviewRepository.save(interview);

        auditLogService.log(officerUsername, "OFFICER/ADMIN", "UPDATE_INTERVIEW",
                "Interview", id.toString(), "Interview updated to status: " + status.getDisplayName(), "N/A");

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interview> getUpcomingInterviewsForStudent(String studentUsername) {
        return interviewRepository.findUpcomingByStudentUsername(studentUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interview> getAllInterviews() {
        return interviewRepository.findAllOrderByInterviewDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interview> getInterviewsByApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        return interviewRepository.findByApplication(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Interview getInterviewById(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
    }
}
