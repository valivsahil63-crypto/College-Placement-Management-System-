package com.college.placement.service;

import com.college.placement.dto.EligibilityResultDto;
import com.college.placement.dto.EligibilityResultDto.StatusType;
import com.college.placement.entity.*;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.enums.RoleType;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.DuplicateApplicationException;
import com.college.placement.exception.IneligibleApplicationException;
import com.college.placement.repository.*;
import com.college.placement.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusHistoryRepository statusHistoryRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private PlacementDriveRepository placementDriveRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EligibilityService eligibilityService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private StudentProfile student;
    private PlacementDrive drive;
    private User studentUser;

    @BeforeEach
    void setUp() {
        studentUser = new User("rahul@college.edu", "pass", "Rahul Sharma", RoleType.ROLE_STUDENT);
        student = new StudentProfile();
        student.setUser(studentUser);
        student.setUsn("1CS21CS045");
        student.setResumePath("resume.pdf");

        Company company = new Company("Google", "Tech", "Bangalore", "HR", "hr@google.com");
        drive = new PlacementDrive();
        drive.setId(10L);
        drive.setTitle("SDE-1");
        drive.setCompany(company);
    }

    @Test
    @DisplayName("Eligible student applying for the first time should successfully save Application")
    void testSuccessfulApplication() {
        when(studentProfileRepository.findByUserUsername("rahul@college.edu")).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(10L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentAndDrive(student, drive)).thenReturn(false);

        EligibilityResultDto eligibleResult = new EligibilityResultDto();
        eligibleResult.setEligible(true);
        eligibleResult.setStatusType(StatusType.ELIGIBLE);
        when(eligibilityService.evaluateEligibility(student, drive)).thenReturn(eligibleResult);

        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application a = invocation.getArgument(0);
            a.setId(100L);
            return a;
        });

        Application result = applicationService.applyForDrive(10L, "rahul@college.edu", "Excited for this opportunity");

        assertNotNull(result);
        assertEquals(ApplicationStatus.APPLIED, result.getCurrentStatus());
        verify(applicationRepository).save(any(Application.class));
        verify(statusHistoryRepository).save(any(ApplicationStatusHistory.class));
    }

    @Test
    @DisplayName("Duplicate application attempt must throw DuplicateApplicationException")
    void testDuplicateApplicationRejected() {
        when(studentProfileRepository.findByUserUsername("rahul@college.edu")).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(10L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentAndDrive(student, drive)).thenReturn(true);

        assertThrows(DuplicateApplicationException.class, () ->
                applicationService.applyForDrive(10L, "rahul@college.edu", "Duplicate attempt"));

        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    @DisplayName("Application attempt without uploaded resume must throw BusinessValidationException")
    void testMissingResumeRejected() {
        student.setResumePath(null); // No resume uploaded

        when(studentProfileRepository.findByUserUsername("rahul@college.edu")).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(10L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentAndDrive(student, drive)).thenReturn(false);

        assertThrows(BusinessValidationException.class, () ->
                applicationService.applyForDrive(10L, "rahul@college.edu", "Missing resume"));
    }

    @Test
    @DisplayName("Ineligible student attempting application must throw IneligibleApplicationException")
    void testIneligibleStudentApplicationRejected() {
        when(studentProfileRepository.findByUserUsername("rahul@college.edu")).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(10L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentAndDrive(student, drive)).thenReturn(false);

        EligibilityResultDto failedResult = new EligibilityResultDto();
        failedResult.setEligible(false);
        failedResult.setStatusType(StatusType.NOT_ELIGIBLE);
        failedResult.addFailure("CGPA below threshold");
        when(eligibilityService.evaluateEligibility(student, drive)).thenReturn(failedResult);

        assertThrows(IneligibleApplicationException.class, () ->
                applicationService.applyForDrive(10L, "rahul@college.edu", "Bypass attempt"));
    }

    @Test
    @DisplayName("Withdrawal allowed when application is in APPLIED status")
    void testWithdrawalInAppliedStatusAllowed() {
        Application app = new Application(student, drive);
        app.setId(100L);
        app.setCurrentStatus(ApplicationStatus.APPLIED);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));

        applicationService.withdrawApplication(100L, "rahul@college.edu", "Offer received elsewhere");

        assertEquals(ApplicationStatus.WITHDRAWN, app.getCurrentStatus());
        verify(applicationRepository).save(app);
    }

    @Test
    @DisplayName("Withdrawal blocked when application is SHORTLISTED")
    void testWithdrawalInShortlistedStatusBlocked() {
        Application app = new Application(student, drive);
        app.setId(100L);
        app.setCurrentStatus(ApplicationStatus.SHORTLISTED);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));

        assertThrows(BusinessValidationException.class, () ->
                applicationService.withdrawApplication(100L, "rahul@college.edu", "Late withdrawal"));
    }
}
