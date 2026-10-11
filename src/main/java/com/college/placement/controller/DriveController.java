package com.college.placement.controller;

import com.college.placement.dto.EligibilityResultDto;
import com.college.placement.dto.PlacementDriveDto;
import com.college.placement.entity.Company;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.StudentProfile;
import com.college.placement.enums.DriveStatus;
import com.college.placement.enums.RoleType;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.service.CompanyService;
import com.college.placement.service.EligibilityService;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.service.StudentProfileService;
import com.college.placement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/drives")
public class DriveController {

    private final PlacementDriveService placementDriveService;
    private final CompanyService companyService;
    private final StudentProfileService studentProfileService;
    private final EligibilityService eligibilityService;

    public DriveController(PlacementDriveService placementDriveService,
                           CompanyService companyService,
                           StudentProfileService studentProfileService,
                           EligibilityService eligibilityService) {
        this.placementDriveService = placementDriveService;
        this.companyService = companyService;
        this.studentProfileService = studentProfileService;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping
    public String listDrives(@RequestParam(value = "status", required = false) DriveStatus status,
                             Authentication authentication,
                             Model model) {
        List<PlacementDrive> drives = placementDriveService.getAllDrives();
        if (status != null) {
            drives = drives.stream().filter(d -> d.getStatus() == status).toList();
        }

        model.addAttribute("drives", drives);
        model.addAttribute("selectedStatus", status);

        boolean isStudent = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_STUDENT.name()));
        model.addAttribute("isStudent", isStudent);

        return "drive/list";
    }

    @GetMapping("/{id}")
    public String viewDriveDetails(@PathVariable("id") Long id,
                                   Authentication authentication,
                                   Model model) {
        PlacementDrive drive = placementDriveService.getDriveById(id);
        model.addAttribute("drive", drive);

        String username = SecurityUtils.getCurrentUsername();
        boolean isStudent = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleType.ROLE_STUDENT.name()));

        if (isStudent && username != null) {
            try {
                StudentProfile student = studentProfileService.getProfileByUsername(username);
                EligibilityResultDto eligibility = eligibilityService.evaluateEligibility(student, drive);
                model.addAttribute("eligibility", eligibility);
                model.addAttribute("student", student);
            } catch (Exception e) {
                model.addAttribute("profileIncomplete", true);
            }
        }

        return "drive/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String newDriveForm(Model model) {
        if (!model.containsAttribute("driveDto")) {
            PlacementDriveDto dto = new PlacementDriveDto();
            dto.setApplicationOpeningDate(LocalDateTime.now().withSecond(0).withNano(0));
            dto.setApplicationClosingDate(LocalDateTime.now().plusDays(7).withSecond(0).withNano(0));
            dto.setEligibleGraduationYear(2025);
            dto.setMinCgpa(6.5);
            dto.setMinTenthPercentage(60.0);
            dto.setMinTwelfthPercentage(60.0);
            dto.setMaxActiveBacklogs(0);
            dto.setRequiredBranches("CSE,ISE,ECE");
            model.addAttribute("driveDto", dto);
        }
        List<Company> companies = companyService.getActiveCompanies();
        model.addAttribute("companies", companies);
        return "drive/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String createDrive(@Valid @ModelAttribute("driveDto") PlacementDriveDto dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("companies", companyService.getActiveCompanies());
            return "drive/form";
        }

        try {
            String currentUser = SecurityUtils.getCurrentUsername();
            PlacementDrive drive = placementDriveService.createDrive(dto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Placement drive '" + drive.getTitle() + "' created successfully!");
            return "redirect:/drives/" + drive.getId();
        } catch (BusinessValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("companies", companyService.getActiveCompanies());
            return "drive/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String editDriveForm(@PathVariable("id") Long id, Model model) {
        PlacementDrive drive = placementDriveService.getDriveById(id);
        PlacementDriveDto dto = new PlacementDriveDto();
        dto.setId(drive.getId());
        dto.setTitle(drive.getTitle());
        dto.setDescription(drive.getDescription());
        dto.setCompanyId(drive.getCompany().getId());
        dto.setJobLocation(drive.getJobLocation());
        dto.setJobType(drive.getJobType());
        dto.setRequiredBranches(drive.getRequiredBranches());
        dto.setMinCgpa(drive.getMinCgpa());
        dto.setMinTenthPercentage(drive.getMinTenthPercentage());
        dto.setMinTwelfthPercentage(drive.getMinTwelfthPercentage());
        dto.setMaxActiveBacklogs(drive.getMaxActiveBacklogs());
        dto.setEligibleGraduationYear(drive.getEligibleGraduationYear());
        dto.setRequiredSkills(drive.getRequiredSkills());
        dto.setCtcSalary(drive.getCtcSalary());
        dto.setApplicationOpeningDate(drive.getApplicationOpeningDate());
        dto.setApplicationClosingDate(drive.getApplicationClosingDate());
        dto.setVacancies(drive.getVacancies());
        dto.setSelectionProcess(drive.getSelectionProcess());
        dto.setStatus(drive.getStatus());

        model.addAttribute("driveDto", dto);
        model.addAttribute("companies", companyService.getActiveCompanies());
        model.addAttribute("isEdit", true);
        return "drive/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String updateDrive(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("driveDto") PlacementDriveDto dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("companies", companyService.getActiveCompanies());
            model.addAttribute("isEdit", true);
            return "drive/form";
        }

        try {
            String currentUser = SecurityUtils.getCurrentUsername();
            PlacementDrive drive = placementDriveService.updateDrive(id, dto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Placement drive updated successfully!");
            return "redirect:/drives/" + drive.getId();
        } catch (BusinessValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("companies", companyService.getActiveCompanies());
            model.addAttribute("isEdit", true);
            return "drive/form";
        }
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") DriveStatus status,
                               RedirectAttributes redirectAttributes) {
        String currentUser = SecurityUtils.getCurrentUsername();
        placementDriveService.updateDriveStatus(id, status, currentUser);
        redirectAttributes.addFlashAttribute("successMessage",
                "Drive status updated to: " + status.getDisplayName());
        return "redirect:/drives/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDrive(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        String adminUser = SecurityUtils.getCurrentUsername();
        placementDriveService.deleteDrive(id, adminUser);
        redirectAttributes.addFlashAttribute("successMessage", "Placement drive deleted successfully.");
        return "redirect:/drives";
    }
}
