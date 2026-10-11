package com.college.placement.service.impl;

import com.college.placement.dto.StudentRegistrationDto;
import com.college.placement.entity.StudentProfile;
import com.college.placement.entity.User;
import com.college.placement.enums.RoleType;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.StudentProfileRepository;
import com.college.placement.repository.UserRepository;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.NotificationService;
import com.college.placement.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public UserServiceImpl(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           PasswordEncoder passwordEncoder,
                           NotificationService notificationService,
                           AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public User registerStudent(StudentRegistrationDto dto) {
        // 1. Password confirmation check
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessValidationException("Password and confirm password do not match.");
        }

        // 2. Email uniqueness check
        if (userRepository.existsByUsername(dto.getEmail())) {
            throw new BusinessValidationException("A user with this email address already exists.");
        }

        // 3. USN uniqueness check
        if (studentProfileRepository.existsByUsn(dto.getUsn().trim().toUpperCase())) {
            throw new BusinessValidationException("A student with USN " + dto.getUsn() + " already exists.");
        }

        // 4. Create User
        User user = new User();
        user.setUsername(dto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName().trim());
        user.setRole(RoleType.ROLE_STUDENT);
        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        // 5. Create StudentProfile
        StudentProfile profile = new StudentProfile();
        profile.setUser(savedUser);
        profile.setUsn(dto.getUsn().trim().toUpperCase());
        profile.setPhone(dto.getPhone().trim());
        profile.setGender(dto.getGender());
        profile.setBranch(dto.getBranch());
        profile.setGraduationYear(dto.getGraduationYear());
        profile.setCgpa(dto.getCgpa());
        profile.setTenthPercentage(dto.getTenthPercentage());
        profile.setTwelfthPercentage(dto.getTwelfthPercentage());
        profile.setActiveBacklogs(dto.getActiveBacklogs());
        profile.setSkills(dto.getSkills());
        profile.setAddress(dto.getAddress());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setGithubUrl(dto.getGithubUrl());
        studentProfileRepository.save(profile);

        // 6. Notification & Audit Log
        notificationService.sendNotification(savedUser, "Welcome to Placement Portal",
                "Welcome to the College Placement Portal, " + savedUser.getFullName() +
                        "! Please ensure your academic profile and resume are up to date.",
                "/student/profile");

        auditLogService.log(savedUser.getUsername(), savedUser.getRole().name(), "STUDENT_REGISTRATION",
                "User", savedUser.getId().toString(), "New student registered: " + savedUser.getUsername(), "N/A");

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessValidationException("Current password is incorrect.");
        }
        if (newPassword.length() < 6) {
            throw new BusinessValidationException("New password must be at least 6 characters.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogService.log(username, user.getRole().name(), "PASSWORD_CHANGE",
                "User", user.getId().toString(), "Password changed successfully", "N/A");
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, String adminUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        auditLogService.log(adminUsername, "ROLE_ADMIN", "TOGGLE_USER_STATUS",
                "User", userId.toString(), "User " + user.getUsername() + " status changed to: " + (user.isEnabled() ? "Active" : "Deactivated"), "N/A");
    }
}
