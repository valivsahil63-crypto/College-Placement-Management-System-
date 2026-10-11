package com.college.placement.service;

import com.college.placement.dto.EligibilityResultDto;
import com.college.placement.dto.EligibilityResultDto.StatusType;
import com.college.placement.entity.Company;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.StudentProfile;
import com.college.placement.entity.User;
import com.college.placement.enums.DriveStatus;
import com.college.placement.enums.JobType;
import com.college.placement.enums.RoleType;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.service.impl.EligibilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private EligibilityServiceImpl eligibilityService;

    private StudentProfile student;
    private PlacementDrive drive;

    @BeforeEach
    void setUp() {
        User user = new User("student@college.edu", "pass", "Test Student", RoleType.ROLE_STUDENT);
        student = new StudentProfile();
        student.setUser(user);
        student.setUsn("1CS21CS001");
        student.setBranch("CSE");
        student.setCgpa(8.5);
        student.setTenthPercentage(85.0);
        student.setTwelfthPercentage(85.0);
        student.setActiveBacklogs(0);
        student.setGraduationYear(2025);
        student.setResumePath("test_resume.pdf");

        Company company = new Company("Test Corp", "IT", "Bengaluru", "HR", "hr@test.com");
        drive = new PlacementDrive();
        drive.setId(1L);
        drive.setCompany(company);
        drive.setTitle("Software Engineer");
        drive.setJobType(JobType.FULL_TIME);
        drive.setRequiredBranches("CSE,ISE");
        drive.setMinCgpa(7.5);
        drive.setMinTenthPercentage(70.0);
        drive.setMinTwelfthPercentage(70.0);
        drive.setMaxActiveBacklogs(0);
        drive.setEligibleGraduationYear(2025);
        drive.setCtcSalary(12.0);
        drive.setStatus(DriveStatus.PUBLISHED);
        drive.setApplicationOpeningDate(LocalDateTime.now().minusDays(2));
        drive.setApplicationClosingDate(LocalDateTime.now().plusDays(5));

        when(applicationRepository.existsByStudentAndDrive(any(), any())).thenReturn(false);
    }

    @Test
    @DisplayName("Student meeting all academic criteria should be ELIGIBLE")
    void testEligibleStudent() {
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertTrue(result.isEligible());
        assertEquals(StatusType.ELIGIBLE, result.getStatusType());
        assertTrue(result.getFailedCriteria().isEmpty());
    }

    @Test
    @DisplayName("Boundary Condition: CGPA exactly equal to threshold should be ELIGIBLE")
    void testCgpaExactBoundaryCondition() {
        student.setCgpa(7.50); // Exactly minCgpa
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertTrue(result.isEligible());
        assertEquals(StatusType.ELIGIBLE, result.getStatusType());
    }

    @Test
    @DisplayName("Boundary Condition: CGPA just below threshold (7.49 < 7.50) should be INELIGIBLE")
    void testCgpaBelowThreshold() {
        student.setCgpa(7.49);
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.NOT_ELIGIBLE, result.getStatusType());
        assertTrue(result.getFailedCriteria().stream().anyMatch(r -> r.contains("CGPA 7.49 is below")));
    }

    @Test
    @DisplayName("Student with backlogs exceeding limit should be INELIGIBLE")
    void testActiveBacklogsExceeded() {
        student.setActiveBacklogs(1); // Drive allows max 0
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.NOT_ELIGIBLE, result.getStatusType());
        assertTrue(result.getFailedCriteria().stream().anyMatch(r -> r.contains("Active backlogs (1) exceeds")));
    }

    @Test
    @DisplayName("Student from non-eligible branch should be INELIGIBLE")
    void testBranchMismatch() {
        student.setBranch("MECH"); // Drive requires CSE, ISE
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.NOT_ELIGIBLE, result.getStatusType());
        assertTrue(result.getFailedCriteria().stream().anyMatch(r -> r.contains("Branch 'MECH' is not in the allowed list")));
    }

    @Test
    @DisplayName("Student with graduation year mismatch should be INELIGIBLE")
    void testGraduationYearMismatch() {
        student.setGraduationYear(2026); // Drive requires 2025
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.NOT_ELIGIBLE, result.getStatusType());
        assertTrue(result.getFailedCriteria().stream().anyMatch(r -> r.contains("Graduation batch 2026 does not match")));
    }

    @Test
    @DisplayName("Application after closing deadline should return APPLICATION_CLOSED")
    void testDriveDeadlinePassed() {
        drive.setApplicationClosingDate(LocalDateTime.now().minusHours(1)); // Closed 1 hour ago
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.APPLICATION_CLOSED, result.getStatusType());
    }

    @Test
    @DisplayName("Application before opening date should return NOT_YET_OPEN")
    void testDriveNotYetOpen() {
        drive.setApplicationOpeningDate(LocalDateTime.now().plusDays(2)); // Opens in 2 days
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.NOT_YET_OPEN, result.getStatusType());
    }

    @Test
    @DisplayName("Already applied student should return ALREADY_APPLIED")
    void testAlreadyAppliedStudent() {
        when(applicationRepository.existsByStudentAndDrive(student, drive)).thenReturn(true);
        EligibilityResultDto result = eligibilityService.evaluateEligibility(student, drive);

        assertFalse(result.isEligible());
        assertEquals(StatusType.ALREADY_APPLIED, result.getStatusType());
    }
}
