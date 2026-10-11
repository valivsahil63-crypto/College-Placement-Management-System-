package com.college.placement.controller;

import com.college.placement.dto.AnnouncementDto;
import com.college.placement.dto.DashboardStatsDto;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.service.NotificationService;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/officer")
@PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
public class OfficerController {

    private final ReportService reportService;
    private final PlacementDriveService placementDriveService;
    private final NotificationService notificationService;

    public OfficerController(ReportService reportService,
                             PlacementDriveService placementDriveService,
                             NotificationService notificationService) {
        this.reportService = reportService;
        this.placementDriveService = placementDriveService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsDto stats = reportService.getDashboardStats();
        List<PlacementDrive> activeDrives = placementDriveService.getActiveDrives();

        model.addAttribute("stats", stats);
        model.addAttribute("activeDrives", activeDrives);
        return "officer/dashboard";
    }

    @GetMapping("/announcements")
    public String announcementsPage(Model model) {
        if (!model.containsAttribute("announcementDto")) {
            model.addAttribute("announcementDto", new AnnouncementDto());
        }
        return "officer/announcements";
    }

    @PostMapping("/announcements")
    public String broadcastAnnouncement(@Valid @ModelAttribute("announcementDto") AnnouncementDto dto,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "officer/announcements";
        }

        notificationService.broadcastToAllStudents(
                dto.getTitle().trim(),
                dto.getMessage().trim(),
                dto.getLink() != null && !dto.getLink().isBlank() ? dto.getLink().trim() : "/student/dashboard"
        );

        redirectAttributes.addFlashAttribute("successMessage",
                "Campus announcement broadcasted to all registered students successfully!");
        return "redirect:/officer/announcements";
    }
}
