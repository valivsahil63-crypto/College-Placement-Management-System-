package com.college.placement.security;

import com.college.placement.entity.AuditLog;
import com.college.placement.repository.AuditLogRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuditLogRepository auditLogRepository;

    public CustomAuthenticationSuccessHandler(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String role = "";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            role = authority.getAuthority();
        }

        // Audit login event
        try {
            AuditLog log = new AuditLog(
                    authentication.getName(),
                    role,
                    "USER_LOGIN",
                    "User",
                    authentication.getName(),
                    "User logged in successfully",
                    request.getRemoteAddr()
            );
            auditLogRepository.save(log);
        } catch (Exception ignored) {
        }

        if (role.equals("ROLE_ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (role.equals("ROLE_OFFICER")) {
            response.sendRedirect(request.getContextPath() + "/officer/dashboard");
        } else if (role.equals("ROLE_STUDENT")) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
