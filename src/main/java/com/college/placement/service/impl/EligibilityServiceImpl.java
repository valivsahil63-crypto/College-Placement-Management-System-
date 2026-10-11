package com.college.placement.service.impl;

import com.college.placement.dto.EligibilityResultDto;
import com.college.placement.dto.EligibilityResultDto.StatusType;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.StudentProfile;
import com.college.placement.enums.DriveStatus;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.service.EligibilityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EligibilityServiceImpl implements EligibilityService {

    private final ApplicationRepository applicationRepository;

    public EligibilityServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public EligibilityResultDto evaluateEligibility(StudentProfile student, PlacementDrive drive) {
        EligibilityResultDto result = new EligibilityResultDto();

        if (student == null || drive == null) {
            result.setEligible(false);
            result.setStatusType(StatusType.NOT_ELIGIBLE);
            result.addFailure("Student profile or placement drive information is missing.");
            return result;
        }

        // Set boundary metrics for diagnostic display
        result.setStudentCgpa(student.getCgpa());
        result.setMinCgpa(drive.getMinCgpa());
        result.setStudentTenth(student.getTenthPercentage());
        result.setMinTenth(drive.getMinTenthPercentage());
        result.setStudentTwelfth(student.getTwelfthPercentage());
        result.setMinTwelfth(drive.getMinTwelfthPercentage());
        result.setStudentBacklogs(student.getActiveBacklogs());
        result.setMaxBacklogs(drive.getMaxActiveBacklogs());
        result.setStudentBranch(student.getBranch());
        result.setAllowedBranches(drive.getRequiredBranches());
        result.setStudentGradYear(student.getGraduationYear());
        result.setEligibleGradYear(drive.getEligibleGraduationYear());

        boolean hasResume = student.getResumePath() != null && !student.getResumePath().trim().isEmpty();
        result.setResumeUploaded(hasResume);

        // Check if student already applied
        boolean alreadyApplied = applicationRepository.existsByStudentAndDrive(student, drive);
        result.setAlreadyApplied(alreadyApplied);

        LocalDateTime now = LocalDateTime.now();

        // 1. Check drive publishing status
        if (drive.getStatus() != DriveStatus.PUBLISHED) {
            result.setEligible(false);
            result.setStatusType(StatusType.APPLICATION_CLOSED);
            result.addFailure("Drive is currently " + drive.getStatus().getDisplayName() + " and not open for student applications.");
            return result;
        }

        // 2. Check timing window: not yet open
        if (drive.getApplicationOpeningDate() != null && now.isBefore(drive.getApplicationOpeningDate())) {
            result.setEligible(false);
            result.setStatusType(StatusType.NOT_YET_OPEN);
            result.addFailure("Application window opens on " + drive.getApplicationOpeningDate().toString().replace("T", " ") + ".");
            return result;
        }

        // 3. Check timing window: deadline passed
        if (drive.getApplicationClosingDate() != null && now.isAfter(drive.getApplicationClosingDate())) {
            result.setEligible(false);
            result.setStatusType(StatusType.APPLICATION_CLOSED);
            result.addFailure("Application deadline closed on " + drive.getApplicationClosingDate().toString().replace("T", " ") + ".");
            return result;
        }

        // If already applied, report that status
        if (alreadyApplied) {
            result.setEligible(false);
            result.setStatusType(StatusType.ALREADY_APPLIED);
            result.addFailure("You have already submitted an application for this placement drive.");
            return result;
        }

        // 4. Academic Criteria Evaluation (Boundary Conditions)
        // A. CGPA (Inclusive comparison: student.cgpa >= drive.minCgpa)
        if (student.getCgpa() == null || student.getCgpa() < drive.getMinCgpa()) {
            result.addFailure(String.format("CGPA %.2f is below the required minimum of %.2f",
                    student.getCgpa() != null ? student.getCgpa() : 0.0, drive.getMinCgpa()));
        } else {
            result.addSatisfied(String.format("CGPA %.2f meets requirement (>= %.2f)",
                    student.getCgpa(), drive.getMinCgpa()));
        }

        // B. 10th Grade Percentage (Inclusive comparison)
        if (student.getTenthPercentage() == null || student.getTenthPercentage() < drive.getMinTenthPercentage()) {
            result.addFailure(String.format("10th percentage %.2f%% is below the required %.2f%%",
                    student.getTenthPercentage() != null ? student.getTenthPercentage() : 0.0, drive.getMinTenthPercentage()));
        } else {
            result.addSatisfied(String.format("10th percentage %.2f%% meets requirement (>= %.2f%%)",
                    student.getTenthPercentage(), drive.getMinTenthPercentage()));
        }

        // C. 12th Grade Percentage (Inclusive comparison)
        if (student.getTwelfthPercentage() == null || student.getTwelfthPercentage() < drive.getMinTwelfthPercentage()) {
            result.addFailure(String.format("12th percentage %.2f%% is below the required %.2f%%",
                    student.getTwelfthPercentage() != null ? student.getTwelfthPercentage() : 0.0, drive.getMinTwelfthPercentage()));
        } else {
            result.addSatisfied(String.format("12th percentage %.2f%% meets requirement (>= %.2f%%)",
                    student.getTwelfthPercentage(), drive.getMinTwelfthPercentage()));
        }

        // D. Branch Eligibility
        if (drive.getRequiredBranches() != null && !drive.getRequiredBranches().trim().isEmpty()) {
            List<String> allowedBranches = Arrays.stream(drive.getRequiredBranches().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            boolean branchMatches = isBranchAllowed(student.getBranch(), allowedBranches);
            if (!branchMatches) {
                result.addFailure(String.format("Branch '%s' is not in the allowed list: %s",
                        student.getBranch(), String.join(", ", allowedBranches)));
            } else {
                result.addSatisfied(String.format("Branch '%s' is eligible", student.getBranch()));
            }
        }

        // E. Graduation Year
        if (student.getGraduationYear() == null ||
                !student.getGraduationYear().equals(drive.getEligibleGraduationYear())) {
            result.addFailure(String.format("Graduation batch %s does not match required batch %d",
                    student.getGraduationYear() != null ? student.getGraduationYear().toString() : "N/A",
                    drive.getEligibleGraduationYear()));
        } else {
            result.addSatisfied(String.format("Graduation batch %d is eligible", student.getGraduationYear()));
        }

        // F. Active Backlogs (Inclusive comparison: student.backlogs <= drive.maxBacklogs)
        if (student.getActiveBacklogs() == null || student.getActiveBacklogs() > drive.getMaxActiveBacklogs()) {
            result.addFailure(String.format("Active backlogs (%d) exceeds the maximum allowed (%d)",
                    student.getActiveBacklogs() != null ? student.getActiveBacklogs() : 0, drive.getMaxActiveBacklogs()));
        } else {
            result.addSatisfied(String.format("Active backlogs (%d) within permissible limit (<= %d)",
                    student.getActiveBacklogs(), drive.getMaxActiveBacklogs()));
        }

        // Final decision
        if (result.getFailedCriteria().isEmpty()) {
            result.setEligible(true);
            result.setStatusType(StatusType.ELIGIBLE);
        } else {
            result.setEligible(false);
            result.setStatusType(StatusType.NOT_ELIGIBLE);
        }

        return result;
    }

    private boolean isBranchAllowed(String studentBranch, List<String> allowedBranches) {
        if (studentBranch == null || studentBranch.trim().isEmpty()) {
            return false;
        }
        String cleanStudentBranch = studentBranch.trim().toLowerCase();

        for (String allowed : allowedBranches) {
            String cleanAllowed = allowed.trim().toLowerCase();
            if (cleanStudentBranch.equals(cleanAllowed) ||
                cleanStudentBranch.contains(cleanAllowed) ||
                cleanAllowed.contains(cleanStudentBranch)) {
                return true;
            }
            // Standard acronym aliases (CSE, ISE, ECE, MECH, CIVIL)
            if (matchBranchAcronym(cleanStudentBranch, cleanAllowed)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchBranchAcronym(String student, String target) {
        if ((student.contains("computer") || student.equals("cse") || student.equals("cs")) &&
            (target.equals("cse") || target.contains("computer") || target.equals("cs"))) {
            return true;
        }
        if ((student.contains("information") || student.equals("ise") || student.equals("it")) &&
            (target.equals("ise") || target.contains("information") || target.equals("it"))) {
            return true;
        }
        if ((student.contains("electronics") || student.equals("ece") || student.equals("ec")) &&
            (target.equals("ece") || target.contains("electronics") || target.equals("ec"))) {
            return true;
        }
        if ((student.contains("mechanical") || student.equals("mech") || student.equals("me")) &&
            (target.equals("mech") || target.contains("mechanical") || target.equals("me"))) {
            return true;
        }
        if ((student.contains("civil") || student.equals("civil") || student.equals("cv")) &&
            (target.equals("civil") || target.contains("civil") || target.equals("cv"))) {
            return true;
        }
        return false;
    }
}
