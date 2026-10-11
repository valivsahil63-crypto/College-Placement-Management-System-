package com.college.placement.controller;

import com.college.placement.dto.InterviewScheduleDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.Interview;
import com.college.placement.enums.InterviewMode;
import com.college.placement.enums.InterviewStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.InterviewService;
import com.college.placement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class InterviewController {

    private final InterviewService interviewService;
    private final ApplicationService applicationService;

    public InterviewController(InterviewService interviewService, ApplicationService applicationService) {
        this.interviewService = interviewService;
        this.applicationService = applicationService;
    }

    @GetMapping("/officer/interviews")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String listInterviews(@RequestParam(value = "appId", required = false) Long appId, Model model) {
        List<Interview> interviews = interviewService.getAllInterviews();
        model.addAttribute("interviews", interviews);
        model.addAttribute("statuses", InterviewStatus.values());

        InterviewScheduleDto scheduleDto = new InterviewScheduleDto();
        if (appId != null) {
            scheduleDto.setApplicationId(appId);
            Application app = applicationService.getApplicationById(appId);
            model.addAttribute("selectedApp", app);
        }
        scheduleDto.setInterviewDate(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        model.addAttribute("scheduleDto", scheduleDto);
        model.addAttribute("modes", InterviewMode.values());

        return "interview/list";
    }

    @PostMapping("/officer/interviews/schedule")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String scheduleInterview(@Valid @ModelAttribute("scheduleDto") InterviewScheduleDto dto,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid interview scheduling input.");
            return "redirect:/officer/interviews";
        }

        try {
            String officerUsername = SecurityUtils.getCurrentUsername();
            interviewService.scheduleInterview(dto, officerUsername);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Interview round '" + dto.getRoundName() + "' scheduled successfully!");
        } catch (BusinessValidationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/officer/interviews";
    }

    @PostMapping("/officer/interviews/{id}/update")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public String updateInterviewStatus(@PathVariable("id") Long id,
                                        @RequestParam("status") InterviewStatus status,
                                        @RequestParam(value = "feedback", required = false) String feedback,
                                        RedirectAttributes redirectAttributes) {
        String officerUsername = SecurityUtils.getCurrentUsername();
        interviewService.updateInterview(id, status, feedback, officerUsername);
        redirectAttributes.addFlashAttribute("successMessage", "Interview status updated successfully.");
        return "redirect:/officer/interviews";
    }

    @GetMapping("/student/interviews")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentInterviews(Model model) {
        String studentUsername = SecurityUtils.getCurrentUsername();
        List<Interview> interviews = interviewService.getUpcomingInterviewsForStudent(studentUsername);
        model.addAttribute("interviews", interviews);
        return "student/interviews";
    }
}
