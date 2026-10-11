package com.college.placement.service;

import com.college.placement.dto.DashboardStatsDto;

public interface ReportService {
    DashboardStatsDto getDashboardStats();
    byte[] generateStudentsCsv();
    byte[] generatePlacedStudentsCsv();
    byte[] generateDriveApplicationsCsv(Long driveId);
}
