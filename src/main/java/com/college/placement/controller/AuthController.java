package com.college.placement.controller;

import com.college.placement.dto.StudentRegistrationDto;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final PlacementDriveService placementDriveService;

    public AuthController(UserService userService, PlacementDriveService placementDriveService) {
        this.userService = userService;
        this.placementDriveService = placementDriveService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOfficer = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OFFICER"));
            boolean isStudent = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

            if (isAdmin) return "redirect:/admin/dashboard";
            if (isOfficer) return "redirect:/officer/dashboard";
            if (isStudent) return "redirect:/student/dashboard";
        }

        List<PlacementDrive> activeDrives = placementDriveService.getActiveDrives();
        model.addAttribute("activeDrives", activeDrives);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registrationDto")) {
            model.addAttribute("registrationDto", new StudentRegistrationDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registrationDto") StudentRegistrationDto dto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerStudent(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! You can now log in with your college email credentials.");
            return "redirect:/login";
        } catch (BusinessValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms-and-conditions")
    public String termsAndConditions() {
        return "terms-and-conditions";
    }
}
