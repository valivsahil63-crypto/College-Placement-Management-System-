package com.college.placement.controller;

import com.college.placement.config.CommonAttributesControllerAdvice;
import com.college.placement.config.SecurityConfig;
import com.college.placement.security.CustomAuthenticationSuccessHandler;
import com.college.placement.security.CustomUserDetailsService;
import com.college.placement.service.NotificationService;
import com.college.placement.service.PlacementDriveService;
import com.college.placement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, CommonAttributesControllerAdvice.class})
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PlacementDriveService placementDriveService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Public root landing page '/' should be accessible by anonymous users")
    void testPublicRootPage() throws Exception {
        when(placementDriveService.getActiveDrives()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Login page '/login' should be accessible by anonymous users")
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("Student registration page '/register' should be accessible by anonymous users")
    void testRegisterPageAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("Unauthenticated request to protected admin area should redirect to '/login'")
    void testAdminAccessWithoutAuthRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "student@college.edu", roles = {"STUDENT"})
    @DisplayName("Student attempting to access admin dashboard should be denied (403 Forbidden)")
    void testStudentForbiddenFromAdminArea() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Privacy Policy page '/privacy-policy' should be accessible by anonymous users")
    void testPrivacyPolicyAccessible() throws Exception {
        mockMvc.perform(get("/privacy-policy"))
                .andExpect(status().isOk())
                .andExpect(view().name("privacy-policy"));
    }

    @Test
    @DisplayName("Terms and Conditions page '/terms-and-conditions' should be accessible by anonymous users")
    void testTermsAndConditionsAccessible() throws Exception {
        mockMvc.perform(get("/terms-and-conditions"))
                .andExpect(status().isOk())
                .andExpect(view().name("terms-and-conditions"));
    }
}
