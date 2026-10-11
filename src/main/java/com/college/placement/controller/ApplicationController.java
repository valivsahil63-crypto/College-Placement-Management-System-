package com.college.placement.controller;

import com.college.placement.dto.StatusUpdateDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.ApplicationStatusHistory;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.DuplicateApplicationException;
import com.college.placement.exception.IneligibleApplicationException;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ApplicationController {

    private final ApplicationService applicationService;
    private final PlacementDriveService placementDriveService;

    public ApplicationController(ApplicationService applicationService,
                                 PlacementDriveService placementDriveService) {
        this.applicationService = applicationService;
        this.placementDriveService = placementDriveService;
    }

    @PostMapping("/applications/apply/{driveId}")
    @PreAuthorize("hasRole('STUDENT')")
    public String apply(@PathVariable("driveId") Long driveId,
                        @RequestParam(value = "remarks", required = false) String remarks,
                        RedirectAttributes redirectAttributes) {
        String studentUsername = SecurityUtils.getCurrentUsername();
        try {
            applicationService.applyForDrive(driveId, studentUsername, remarks);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application submitted successfully! Track your application status on this dashboard.");
            return "redirect:/student/applications";
        } catch (DuplicateApplicationException | IneligibleApplicationException | BusinessValidationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/drives/" + driveId;
        }
    }

    @GetMapping("/student/applications")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentApplications(Model model) {
        String studentUsername = SecurityUtils.getCurrentUsername();
        List<Application> applications = applicationService.getStudentApplications(studentUsername);
        model.addAttribute("applications", applications);
        return "student/applications";
    }

    @PostMapping("/applications/{id}/withdraw")
    @PreAuthorize("hasRole('STUDENT')")
    public String withdraw(@PathVariable("id") Long id,
                           @RequestParam(value = "reason", required = false) String reason,
                           RedirectAttributes redirectAttributes) {
        String studentUsername = SecurityUtils.getCurrentUsername();
        try {
            applicationService.withdrawApplication(id, studentUsername, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Application withdrawn successfully.");
        } catch (BusinessValidationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/applications";
    }

    @GetMapping("/officer/applications")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String allApplications(@RequestParam(value = "status", required = false) ApplicationStatus status,
                                  Model model) {
        List<Application> applications = applicationService.getAllApplications();
        if (status != null) {
            applications = applications.stream().filter(a -> a.getCurrentStatus() == status).toList();
        }
        model.addAttribute("applications", applications);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "officer/applications";
    }

    @GetMapping("/officer/drives/{driveId}/applications")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String driveApplications(@PathVariable("driveId") Long driveId, Model model) {
        PlacementDrive drive = placementDriveService.getDriveById(driveId);
        List<Application> applications = applicationService.getDriveApplications(driveId);
        model.addAttribute("drive", drive);
        model.addAttribute("applications", applications);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "officer/drive-applications";
    }

    @PostMapping("/officer/applications/status")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String updateApplicationStatus(@Valid @ModelAttribute StatusUpdateDto dto,
                                          RedirectAttributes redirectAttributes) {
        try {
            String officerUsername = SecurityUtils.getCurrentUsername();
            applicationService.updateApplicationStatus(
                    dto.getApplicationId(), dto.getNewStatus(), dto.getRemarks(), officerUsername
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application status successfully changed to: " + dto.getNewStatus().getDisplayName());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + ex.getMessage());
        }
        return "redirect:/officer/applications";
    }

    @GetMapping("/applications/{id}/history")
    @ResponseBody
    public List<ApplicationStatusHistory> getApplicationHistory(@PathVariable("id") Long id) {
        return applicationService.getStatusHistory(id);
    }
}
