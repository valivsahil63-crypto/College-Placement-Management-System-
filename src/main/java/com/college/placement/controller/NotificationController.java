package com.college.placement.controller;

import com.college.placement.entity.Notification;
import com.college.placement.service.NotificationService;
import com.college.placement.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String viewNotifications(Model model) {
        String username = SecurityUtils.getCurrentUsername();
        List<Notification> notifications = notificationService.getUserNotifications(username);
        model.addAttribute("notifications", notifications);
        return "notification/list";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable("id") Long id,
                           @RequestParam(value = "redirect", required = false) String redirect) {
        String username = SecurityUtils.getCurrentUsername();
        notificationService.markAsRead(id, username);
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllRead(RedirectAttributes redirectAttributes) {
        String username = SecurityUtils.getCurrentUsername();
        notificationService.markAllAsRead(username);
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/notifications";
    }
}
