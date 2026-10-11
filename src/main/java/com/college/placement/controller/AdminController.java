package com.college.placement.controller;

import com.college.placement.dto.DashboardStatsDto;
import com.college.placement.entity.AuditLog;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.StudentProfile;
import com.college.placement.service.*;
import com.college.placement.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReportService reportService;
    private final StudentProfileService studentProfileService;
    private final PlacementDriveService placementDriveService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public AdminController(ReportService reportService,
                           StudentProfileService studentProfileService,
                           PlacementDriveService placementDriveService,
                           UserService userService,
                           AuditLogService auditLogService) {
        this.reportService = reportService;
        this.studentProfileService = studentProfileService;
        this.placementDriveService = placementDriveService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsDto stats = reportService.getDashboardStats();
        List<PlacementDrive> recentDrives = placementDriveService.getAllDrives();
        List<StudentProfile> recentStudents = studentProfileService.getAllStudents();

        model.addAttribute("stats", stats);
        model.addAttribute("recentDrives", recentDrives.stream().limit(5).toList());
        model.addAttribute("recentStudents", recentStudents.stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String manageStudents(@RequestParam(value = "query", required = false) String query,
                                 @RequestParam(value = "branch", required = false) String branch,
                                 @RequestParam(value = "gradYear", required = false) Integer gradYear,
                                 Model model) {
        List<StudentProfile> students = studentProfileService.searchStudents(query, branch, gradYear);
        model.addAttribute("students", students);
        model.addAttribute("query", query);
        model.addAttribute("branch", branch);
        model.addAttribute("gradYear", gradYear);
        return "admin/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudentDetails(@PathVariable("id") Long id, Model model) {
        StudentProfile student = studentProfileService.getProfileById(id);
        model.addAttribute("student", student);
        return "admin/student-detail";
    }

    @PostMapping("/students/{id}/toggle-status")
    public String toggleStudentStatus(@PathVariable("id") Long id,
                                      RedirectAttributes redirectAttributes) {
        StudentProfile student = studentProfileService.getProfileById(id);
        String currentAdmin = SecurityUtils.getCurrentUsername();
        userService.toggleUserStatus(student.getUser().getId(), currentAdmin);

        redirectAttributes.addFlashAttribute("successMessage",
                "User account status updated for " + student.getUser().getFullName());
        return "redirect:/admin/students";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        List<AuditLog> logs = auditLogService.getRecentLogs();
        model.addAttribute("logs", logs);
        return "admin/audit-logs";
    }
}
