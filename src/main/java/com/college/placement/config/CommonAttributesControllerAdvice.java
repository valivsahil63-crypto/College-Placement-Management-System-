package com.college.placement.config;

import com.college.placement.security.CustomUserDetails;
import com.college.placement.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonAttributesControllerAdvice {

    private final NotificationService notificationService;

    public CommonAttributesControllerAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    @ModelAttribute("currentUserFullName")
    public String currentUserFullName(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getFullName();
        }
        return null;
    }

    @ModelAttribute("currentUserRole")
    public String currentUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getRoleName();
        }
        return null;
    }

    @ModelAttribute("unreadNotificationsCount")
    public long unreadNotificationsCount(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            return notificationService.getUnreadCount(authentication.getName());
        }
        return 0;
    }
}
