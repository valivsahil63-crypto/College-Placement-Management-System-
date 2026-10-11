package com.college.placement.controller;

import com.college.placement.dto.DashboardStatsDto;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.entity.StudentProfile;
import com.college.placement.service.PlacementOfferService;
import com.college.placement.service.ReportService;
import com.college.placement.service.StudentProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
public class ReportController {

    private final ReportService reportService;
    private final StudentProfileService studentProfileService;
    private final PlacementOfferService placementOfferService;

    public ReportController(ReportService reportService,
                            StudentProfileService studentProfileService,
                            PlacementOfferService placementOfferService) {
        this.reportService = reportService;
        this.studentProfileService = studentProfileService;
        this.placementOfferService = placementOfferService;
    }

    @GetMapping
    public String reportsPage(Model model) {
        DashboardStatsDto stats = reportService.getDashboardStats();
        List<PlacementOffer> offers = placementOfferService.getAllOffers();
        List<StudentProfile> students = studentProfileService.getAllStudents();

        model.addAttribute("stats", stats);
        model.addAttribute("offers", offers);
        model.addAttribute("students", students);
        return "report/reports";
    }

    @GetMapping("/export/students")
    public ResponseEntity<byte[]> exportStudents() {
        byte[] csvData = reportService.generateStudentsCsv();
        String filename = "students_placement_report_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    @GetMapping("/export/placed")
    public ResponseEntity<byte[]> exportPlacedStudents() {
        byte[] csvData = reportService.generatePlacedStudentsCsv();
        String filename = "placed_students_recruitment_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    @GetMapping("/export/drive/{driveId}")
    public ResponseEntity<byte[]> exportDriveApplications(@PathVariable("driveId") Long driveId) {
        byte[] csvData = reportService.generateDriveApplicationsCsv(driveId);
        String filename = "drive_" + driveId + "_applications_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }
}
