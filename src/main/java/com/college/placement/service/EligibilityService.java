package com.college.placement.service;

import com.college.placement.dto.EligibilityResultDto;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.StudentProfile;

public interface EligibilityService {
    /**
     * Evaluates a student profile against a placement drive's eligibility criteria.
     * Boundary rule:
     * - CGPA: student.cgpa >= drive.minCgpa (inclusive threshold)
     * - 10th: student.tenthPercentage >= drive.minTenthPercentage (inclusive)
     * - 12th: student.twelfthPercentage >= drive.minTwelfthPercentage (inclusive)
     * - Backlogs: student.activeBacklogs <= drive.maxActiveBacklogs (inclusive)
     * - Branch: student's branch must match any branch listed in comma-delimited requiredBranches
     * - Grad Year: student.graduationYear == drive.eligibleGraduationYear
     * - Timing: now between drive.applicationOpeningDate and drive.applicationClosingDate
     * - Status: drive.status == PUBLISHED
     */
    EligibilityResultDto evaluateEligibility(StudentProfile student, PlacementDrive drive);
}
