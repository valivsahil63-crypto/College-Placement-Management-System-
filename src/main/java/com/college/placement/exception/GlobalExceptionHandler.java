package com.college.placement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("status", 404);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public String handleDuplicateApplication(DuplicateApplicationException ex, RedirectAttributes redirectAttributes) {
        log.warn("Duplicate application attempt: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/student/drives";
    }

    @ExceptionHandler(IneligibleApplicationException.class)
    public String handleIneligibleApplication(IneligibleApplicationException ex, RedirectAttributes redirectAttributes) {
        log.warn("Ineligible application attempt: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorReasons", ex.getReasons());
        return "redirect:/student/drives";
    }

    @ExceptionHandler(BusinessValidationException.class)
    public String handleBusinessValidation(BusinessValidationException ex, RedirectAttributes redirectAttributes) {
        log.warn("Business validation error: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unhandled application exception: ", ex);
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred while processing your request. Please try again or contact the Placement Cell.");
        return "error/error";
    }
}
