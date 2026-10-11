package com.college.placement.service.impl;

import com.college.placement.dto.DashboardStatsDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.entity.StudentProfile;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.enums.DriveStatus;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.*;
import com.college.placement.service.ReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementOfferRepository offerRepository;

    public ReportServiceImpl(StudentProfileRepository studentProfileRepository,
                             CompanyRepository companyRepository,
                             PlacementDriveRepository placementDriveRepository,
                             ApplicationRepository applicationRepository,
                             PlacementOfferRepository offerRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.applicationRepository = applicationRepository;
        this.offerRepository = offerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long totalStudents = studentProfileRepository.count();
        long totalCompanies = companyRepository.count();
        long activeDrives = placementDriveRepository.countByStatus(DriveStatus.PUBLISHED);
        long totalApplications = applicationRepository.count();
        long placedStudents = applicationRepository.countDistinctStudentByCurrentStatus(ApplicationStatus.SELECTED);

        stats.setTotalStudents(totalStudents);
        stats.setTotalCompanies(totalCompanies);
        stats.setActiveDrives(activeDrives);
        stats.setTotalApplications(totalApplications);
        stats.setPlacedStudents(placedStudents);

        double placementPct = totalStudents > 0 ? ((double) placedStudents / totalStudents) * 100.0 : 0.0;
        stats.setPlacementPercentage(Math.round(placementPct * 10.0) / 10.0);

        Double avgCtc = offerRepository.findAverageOfferedPackage();
        Double highestCtc = offerRepository.findHighestOfferedPackage();
        stats.setAverageCtc(avgCtc != null ? Math.round(avgCtc * 100.0) / 100.0 : 0.0);
        stats.setHighestCtc(highestCtc != null ? Math.round(highestCtc * 100.0) / 100.0 : 0.0);

        // Department total breakdown
        Map<String, Long> deptTotal = new HashMap<>();
        for (Object[] row : studentProfileRepository.countStudentsByDepartment()) {
            if (row[0] != null) {
                deptTotal.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setDepartmentTotal(deptTotal);

        // Department placed breakdown
        Map<String, Long> deptPlaced = new HashMap<>();
        for (Object[] row : studentProfileRepository.countPlacedStudentsByDepartment()) {
            if (row[0] != null) {
                deptPlaced.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setDepartmentPlaced(deptPlaced);

        // Application status breakdown
        Map<String, Long> statusBreakdown = new HashMap<>();
        for (Object[] row : applicationRepository.countApplicationsByStatus()) {
            if (row[0] != null) {
                ApplicationStatus status = (ApplicationStatus) row[0];
                statusBreakdown.put(status.getDisplayName(), (Long) row[1]);
            }
        }
        stats.setStatusBreakdown(statusBreakdown);

        // Company-wise selections
        Map<String, Long> companySelections = new HashMap<>();
        for (Object[] row : applicationRepository.countSelectionsByCompany()) {
            if (row[0] != null) {
                companySelections.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setCompanySelections(companySelections);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateStudentsCsv() {
        List<StudentProfile> students = studentProfileRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("USN", "Full Name", "Email", "Phone", "Branch", "Graduation Year",
                             "CGPA", "10th Percentage", "12th Percentage", "Active Backlogs", "Placed Status")
                     .build())) {

            for (StudentProfile s : students) {
                printer.printRecord(
                        s.getUsn(),
                        s.getUser().getFullName(),
                        s.getUser().getUsername(),
                        s.getPhone(),
                        s.getBranch(),
                        s.getGraduationYear(),
                        s.getCgpa(),
                        s.getTenthPercentage(),
                        s.getTwelfthPercentage(),
                        s.getActiveBacklogs(),
                        s.isPlaced() ? "Placed" : "Unplaced"
                );
            }
            printer.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating student report CSV: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePlacedStudentsCsv() {
        List<PlacementOffer> offers = offerRepository.findAllOrderByOfferDateDesc();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("USN", "Student Name", "Email", "Branch", "Company Name",
                             "Designation", "Offered CTC (LPA)", "Offer Date", "Offer Status")
                     .build())) {

            for (PlacementOffer o : offers) {
                printer.printRecord(
                        o.getStudent().getUsn(),
                        o.getStudent().getUser().getFullName(),
                        o.getStudent().getUser().getUsername(),
                        o.getStudent().getBranch(),
                        o.getCompany().getName(),
                        o.getDesignation(),
                        o.getPackageOffered(),
                        o.getOfferDate(),
                        o.getStatus().getDisplayName()
                );
            }
            printer.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating placed students report CSV: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateDriveApplicationsCsv(Long driveId) {
        PlacementDrive drive = placementDriveRepository.findById(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found with id: " + driveId));
        List<Application> applications = applicationRepository.findByDriveOrderByAppliedAtDesc(drive);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("USN", "Student Name", "Email", "Branch", "CGPA", "Active Backlogs",
                             "Applied Date", "Current Status", "Officer Remarks")
                     .build())) {

            for (Application a : applications) {
                printer.printRecord(
                        a.getStudent().getUsn(),
                        a.getStudent().getUser().getFullName(),
                        a.getStudent().getUser().getUsername(),
                        a.getStudent().getBranch(),
                        a.getStudent().getCgpa(),
                        a.getStudent().getActiveBacklogs(),
                        a.getAppliedAt(),
                        a.getCurrentStatus().getDisplayName(),
                        a.getRemarks() != null ? a.getRemarks() : ""
                );
            }
            printer.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating drive applications CSV: " + e.getMessage(), e);
        }
    }
}
