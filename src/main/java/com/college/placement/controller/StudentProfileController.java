package com.college.placement.controller;

import com.college.placement.dto.OfferCreateDto;
import com.college.placement.dto.StudentProfileUpdateDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.Interview;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.entity.StudentProfile;
import com.college.placement.enums.OfferStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.InterviewService;
import com.college.placement.service.PlacementOfferService;
import com.college.placement.service.StudentProfileService;
import com.college.placement.util.FileUploadUtil;
import com.college.placement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final ApplicationService applicationService;
    private final InterviewService interviewService;
    private final PlacementOfferService placementOfferService;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    public StudentProfileController(StudentProfileService studentProfileService,
                                    ApplicationService applicationService,
                                    InterviewService interviewService,
                                    PlacementOfferService placementOfferService) {
        this.studentProfileService = studentProfileService;
        this.applicationService = applicationService;
        this.interviewService = interviewService;
        this.placementOfferService = placementOfferService;
    }

    @GetMapping("/student/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public String dashboard(Model model) {
        String username = SecurityUtils.getCurrentUsername();
        StudentProfile profile = studentProfileService.getProfileByUsername(username);
        List<Application> applications = applicationService.getStudentApplications(username);
        List<Interview> upcomingInterviews = interviewService.getUpcomingInterviewsForStudent(username);
        List<PlacementOffer> offers = placementOfferService.getOffersForStudent(username);

        model.addAttribute("student", profile);
        model.addAttribute("applications", applications);
        model.addAttribute("upcomingInterviews", upcomingInterviews);
        model.addAttribute("offers", offers);

        return "student/dashboard";
    }

    @GetMapping("/student/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public String profilePage(Model model) {
        String username = SecurityUtils.getCurrentUsername();
        StudentProfile student = studentProfileService.getProfileByUsername(username);

        StudentProfileUpdateDto dto = new StudentProfileUpdateDto();
        dto.setFullName(student.getUser().getFullName());
        dto.setPhone(student.getPhone());
        dto.setGender(student.getGender());
        dto.setBranch(student.getBranch());
        dto.setGraduationYear(student.getGraduationYear());
        dto.setCgpa(student.getCgpa());
        dto.setTenthPercentage(student.getTenthPercentage());
        dto.setTwelfthPercentage(student.getTwelfthPercentage());
        dto.setActiveBacklogs(student.getActiveBacklogs());
        dto.setSkills(student.getSkills());
        dto.setAddress(student.getAddress());
        dto.setLinkedinUrl(student.getLinkedinUrl());
        dto.setGithubUrl(student.getGithubUrl());

        model.addAttribute("student", student);
        model.addAttribute("profileDto", dto);
        return "student/profile";
    }

    @PostMapping("/student/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public String updateProfile(@Valid @ModelAttribute("profileDto") StudentProfileUpdateDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        String username = SecurityUtils.getCurrentUsername();
        if (bindingResult.hasErrors()) {
            StudentProfile student = studentProfileService.getProfileByUsername(username);
            model.addAttribute("student", student);
            return "student/profile";
        }

        studentProfileService.updateProfile(username, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/student/profile";
    }

    @PostMapping("/student/profile/resume")
    @PreAuthorize("hasRole('STUDENT')")
    public String uploadResume(@RequestParam("resumeFile") MultipartFile resumeFile,
                               RedirectAttributes redirectAttributes) {
        String username = SecurityUtils.getCurrentUsername();
        try {
            String savedFileName = FileUploadUtil.saveResume(uploadDir, resumeFile);
            studentProfileService.updateResume(username, savedFileName, resumeFile.getOriginalFilename());
            redirectAttributes.addFlashAttribute("successMessage", "Resume PDF uploaded successfully!");
        } catch (BusinessValidationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to store resume file: " + ex.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/uploads/resume/{fileName:.+}")
    public ResponseEntity<Resource> downloadResume(@PathVariable("fileName") String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        File file = filePath.toFile();

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/student/offers")
    @PreAuthorize("hasRole('STUDENT')")
    public String viewOffers(Model model) {
        String username = SecurityUtils.getCurrentUsername();
        List<PlacementOffer> offers = placementOfferService.getOffersForStudent(username);
        model.addAttribute("offers", offers);
        return "student/offers";
    }

    @PostMapping("/student/offers/{id}/respond")
    @PreAuthorize("hasRole('STUDENT')")
    public String respondToOffer(@PathVariable("id") Long id,
                                 @RequestParam("status") OfferStatus status,
                                 RedirectAttributes redirectAttributes) {
        String username = SecurityUtils.getCurrentUsername();
        try {
            placementOfferService.updateOfferStatus(id, status, username);
            redirectAttributes.addFlashAttribute("successMessage", "Placement offer response recorded successfully.");
        } catch (BusinessValidationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/offers";
    }
}
