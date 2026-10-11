package com.college.placement.service;

import com.college.placement.dto.DashboardStatsDto;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.enums.DriveStatus;
import com.college.placement.repository.*;
import com.college.placement.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PlacementDriveRepository placementDriveRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PlacementOfferRepository offerRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("Dashboard stats should accurately calculate metrics, counts, and placement percentage")
    void testDashboardStatisticsCalculation() {
        // Arrange
        when(studentProfileRepository.count()).thenReturn(100L);
        when(companyRepository.count()).thenReturn(15L);
        when(placementDriveRepository.countByStatus(DriveStatus.PUBLISHED)).thenReturn(8L);
        when(applicationRepository.count()).thenReturn(350L);
        when(applicationRepository.countDistinctStudentByCurrentStatus(ApplicationStatus.SELECTED)).thenReturn(75L);

        when(offerRepository.findAverageOfferedPackage()).thenReturn(14.25);
        when(offerRepository.findHighestOfferedPackage()).thenReturn(32.50);

        when(studentProfileRepository.countStudentsByDepartment()).thenReturn(new ArrayList<>());
        when(studentProfileRepository.countPlacedStudentsByDepartment()).thenReturn(new ArrayList<>());
        when(applicationRepository.countApplicationsByStatus()).thenReturn(new ArrayList<>());
        when(applicationRepository.countSelectionsByCompany()).thenReturn(new ArrayList<>());

        // Act
        DashboardStatsDto stats = reportService.getDashboardStats();

        // Assert
        assertEquals(100L, stats.getTotalStudents());
        assertEquals(15L, stats.getTotalCompanies());
        assertEquals(8L, stats.getActiveDrives());
        assertEquals(350L, stats.getTotalApplications());
        assertEquals(75L, stats.getPlacedStudents());
        assertEquals(75.0, stats.getPlacementPercentage()); // (75 / 100) * 100 = 75.0%
        assertEquals(14.25, stats.getAverageCtc());
        assertEquals(32.50, stats.getHighestCtc());
    }

    @Test
    @DisplayName("Placement percentage should handle zero total students gracefully without division by zero")
    void testZeroTotalStudentsPlacementPercentage() {
        when(studentProfileRepository.count()).thenReturn(0L);
        when(companyRepository.count()).thenReturn(0L);
        when(placementDriveRepository.countByStatus(DriveStatus.PUBLISHED)).thenReturn(0L);
        when(applicationRepository.count()).thenReturn(0L);
        when(applicationRepository.countDistinctStudentByCurrentStatus(ApplicationStatus.SELECTED)).thenReturn(0L);
        when(offerRepository.findAverageOfferedPackage()).thenReturn(null);
        when(offerRepository.findHighestOfferedPackage()).thenReturn(null);

        DashboardStatsDto stats = reportService.getDashboardStats();

        assertEquals(0.0, stats.getPlacementPercentage());
        assertEquals(0.0, stats.getAverageCtc());
        assertEquals(0.0, stats.getHighestCtc());
    }
}
